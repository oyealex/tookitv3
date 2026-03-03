package com.smartkit.toolbox.service;

import com.smartkit.toolbox.invoker.InputFileGenerator;
import com.smartkit.toolbox.invoker.ToolInvokeResult;
import com.smartkit.toolbox.invoker.ToolInvoker;
import com.smartkit.toolbox.invoker.WorkDirectoryManager;
import com.smartkit.toolbox.lock.ToolLockManager;
import com.smartkit.toolbox.manager.ToolManager;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.tool.*;
import com.smartkit.toolbox.monitor.impl.PollingToolExecutionMonitor;
import com.smartkit.toolbox.util.DateTimeUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 工具服务类
 */
@Service
public class ToolService {

    private static final Logger log = LoggerFactory.getLogger(ToolService.class);

    private final ToolManager toolManager;
    private final ToolInvoker toolInvoker;
    private final WorkDirectoryManager workDirectoryManager;
    private final InputFileGenerator inputFileGenerator;
    private final ToolLockManager lockManager;

    @Autowired(required = false)
    private DeviceService deviceService;

    public ToolService(ToolManager toolManager,
                       ToolInvoker toolInvoker,
                       WorkDirectoryManager workDirectoryManager,
                       InputFileGenerator inputFileGenerator,
                       ToolLockManager lockManager) {
        this.toolManager = toolManager;
        this.toolInvoker = toolInvoker;
        this.workDirectoryManager = workDirectoryManager;
        this.inputFileGenerator = inputFileGenerator;
        this.lockManager = lockManager;
    }

    /**
     * 启动时自动扫描工具
     */
    @PostConstruct
    public void init() {
        try {
            log.info("开始初始化工具扫描...");
            toolManager.loadTools();
            log.info("工具扫描完成，共加载 {} 个工具", toolManager.getToolCount());
        } catch (IOException e) {
            log.error("工具扫描失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 手动刷新工具列表
     */
    public void refreshTools() throws IOException {
        toolManager.loadTools();
    }

    /**
     * 获取工具列表
     */
    public List<ToolConfig> listTools() {
        return toolManager.listTools();
    }

    /**
     * 获取工具
     */
    public Optional<ToolConfig> getTool(String toolId) {
        return toolManager.getTool(toolId);
    }

    /**
     * 执行工具
     *
     * @param toolId  工具 ID
     * @param ips    设备 IP 列表
     * @return 执行结果
     */
    public ToolExecuteResult executeTool(String toolId, List<String> ips) {
        long startTime = System.currentTimeMillis();

        // 1. 获取工具配置
        Optional<ToolConfig> toolOpt = toolManager.getTool(toolId);
        if (toolOpt.isEmpty()) {
            return ToolExecuteResult.failure("工具不存在: " + toolId);
        }
        ToolConfig tool = toolOpt.get();

        // 2. 获取设备信息
        List<Device> devices = new ArrayList<>();
        if (deviceService != null && ips != null) {
            for (String ip : ips) {
                Device device = deviceService.getDevice(ip);
                if (device != null) {
                    devices.add(device);
                }
            }
        }

        if (devices.isEmpty()) {
            return ToolExecuteResult.failure("未找到设备");
        }

        // 3. 准备设备信息（包含登录密码）
        List<DeviceInfo> deviceInfos = devices.stream()
                .map(d -> DeviceInfo.builder()
                        .ip(d.getIp())
                        .name(d.getName())
                        .port(22)  // 默认端口
                        .username(d.getUsername())
                        .password(d.getPassword())
                        .build())
                .toList();

        // 4. 获取全局锁和设备锁
        ToolLockManager.LockResult lockResult = lockManager.tryLock(toolId, ips, 5000);
        if (!lockResult.success()) {
            return ToolExecuteResult.failure(lockResult.message());
        }

        try {
            // 5. 创建工作目录
            String timestamp = DateTimeUtil.getDirTimestamp();
            java.nio.file.Path workDir = workDirectoryManager.createWorkDirectory(toolId, timestamp);
            String workDirStr = workDir.toString();

            // 6. 记录启动日志
            ToolOperationLogger.logExecuteStart(toolId, tool.getName(), ips, workDirStr);

            // 7. 生成输入 JSON
            LocalDateTime startDateTime = LocalDateTime.now();
            ToolInput input = ToolInput.builder()
                    .toolId(toolId)
                    .toolName(tool.getName())
                    .startTime(DateTimeUtil.formatForJson(startDateTime))
                    .workDir(workDirStr)
                    .devices(deviceInfos)
                    .build();
            inputFileGenerator.generateInputFile(workDir, input);

            // 8. 启动工具进程
            ToolInvokeResult invokeResult = toolInvoker.invoke(tool, workDirStr,
                    workDir.resolve(InputFileGenerator.INPUT_FILE_NAME).toString());

            if (!invokeResult.isStarted()) {
                ToolOperationLogger.logExecuteFailure(toolId, tool.getName(), invokeResult.getErrorMessage());
                return ToolExecuteResult.failure("启动失败: " + invokeResult.getErrorMessage());
            }

            // 9. 启动监控
            PollingToolExecutionMonitor monitor = new PollingToolExecutionMonitor();
            monitor.startMonitoring(workDirStr);

            // 10. 等待执行完成
            ToolResult toolResult = monitor.waitForCompletion();

            // 11. 计算耗时
            long duration = System.currentTimeMillis() - startTime;

            // 12. 记录完成日志
            String result = toolResult != null ? toolResult.getResult() : "unknown";
            ToolOperationLogger.logExecuteComplete(toolId, tool.getName(), result, duration);

            return ToolExecuteResult.success(toolResult, workDirStr);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("工具执行被中断: toolId={}", toolId, e);
            return ToolExecuteResult.failure("执行被中断");
        } catch (Exception e) {
            log.error("工具执行失败: toolId={}", toolId, e);
            return ToolOperationLogger.logExecuteFailure(toolId, tool.getName(), e.getMessage())
                    ? ToolExecuteResult.failure("执行失败: " + e.getMessage())
                    : ToolExecuteResult.failure("执行失败: " + e.getMessage());
        } finally {
            // 13. 释放锁
            lockManager.unlock();
        }
    }

    /**
     * 获取运维历史记录
     */
    public List<WorkDirectoryManager.WorkDirInfo> getHistory() throws IOException {
        return workDirectoryManager.listWorkDirectories().stream()
                .map(WorkDirectoryManager::parseWorkDirectory)
                .filter(info -> info != null)
                .toList();
    }

    /**
     * 工具执行结果
     */
    public record ToolExecuteResult(boolean success, String message, ToolResult result, String workDir) {
        public static ToolExecuteResult success(ToolResult result, String workDir) {
            return new ToolExecuteResult(true, null, result, workDir);
        }

        public static ToolExecuteResult failure(String message) {
            return new ToolExecuteResult(false, message, null, null);
        }
    }
}