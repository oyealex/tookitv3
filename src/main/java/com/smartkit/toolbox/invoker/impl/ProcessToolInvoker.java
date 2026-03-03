package com.smartkit.toolbox.invoker.impl;

import com.smartkit.toolbox.invoker.ToolInvoker;
import com.smartkit.toolbox.invoker.ToolInvokeResult;
import com.smartkit.toolbox.model.tool.ToolConfig;
import com.smartkit.toolbox.util.PlatformUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进程工具调用器实现
 */
@Component
public class ProcessToolInvoker implements ToolInvoker {

    private static final Logger log = LoggerFactory.getLogger(ProcessToolInvoker.class);

    /**
     * 工具根目录
     */
    private final Path toolsRoot;

    /**
     * 运行的进程存储
     */
    private final Map<String, Process> runningProcesses = new ConcurrentHashMap<>();

    public ProcessToolInvoker() {
        this(Paths.get("tools").toAbsolutePath().normalize());
    }

    public ProcessToolInvoker(Path toolsRoot) {
        this.toolsRoot = toolsRoot;
    }

    @Override
    public ToolInvokeResult invoke(ToolConfig tool, String workDir, String inputFile) {
        String toolId = tool.getId();
        Path toolDir = toolsRoot.resolve(toolId);

        // 获取当前平台的命令
        String command = tool.getCommand().getCommand();
        if (command == null || command.isEmpty()) {
            return ToolInvokeResult.builder()
                    .started(false)
                    .errorMessage("未配置启动命令")
                    .workDir(workDir)
                    .build();
        }

        Path executable = toolDir.resolve(command);
        if (!executable.toFile().exists()) {
            return ToolInvokeResult.builder()
                    .started(false)
                    .errorMessage("可执行文件不存在: " + executable)
                    .workDir(workDir)
                    .build();
        }

        try {
            // 构建命令
            ProcessBuilder processBuilder;
            if (PlatformUtil.isWindows()) {
                // Windows: 使用 cmd /c 执行
                processBuilder = new ProcessBuilder("cmd", "/c", executable.toString(), "--work-dir", workDir);
            } else {
                // Linux/Mac: 直接执行
                processBuilder = new ProcessBuilder(executable.toString(), "--work-dir", workDir);
            }

            // 设置工作目录
            processBuilder.directory(toolDir.toFile());

            // 继承环境变量
            processBuilder.inheritIO();

            // 启动进程
            Process process = processBuilder.start();
            String processId = String.valueOf(process.pid());
            runningProcesses.put(processId, process);

            log.info("启动工具进程: toolId={}, pid={}, workDir={}", toolId, processId, workDir);

            return ToolInvokeResult.builder()
                    .processId(processId)
                    .workDir(workDir)
                    .started(true)
                    .build();

        } catch (IOException e) {
            log.error("启动工具进程失败: {}", e.getMessage(), e);
            return ToolInvokeResult.builder()
                    .started(false)
                    .errorMessage("启动失败: " + e.getMessage())
                    .workDir(workDir)
                    .build();
        }
    }

    @Override
    public String getToolDirectory(String toolId) {
        return toolsRoot.resolve(toolId).toAbsolutePath().toString();
    }

    @Override
    public boolean terminate(String processId) {
        Process process = runningProcesses.get(processId);
        if (process != null) {
            process.destroy();
            runningProcesses.remove(processId);
            log.info("终止工具进程: pid={}", processId);
            return true;
        }
        return false;
    }

    /**
     * 等待进程结束并清理
     */
    public void waitForProcess(String processId) {
        Process process = runningProcesses.get(processId);
        if (process != null) {
            try {
                process.waitFor();
                runningProcesses.remove(processId);
                log.info("工具进程结束: pid={}, exitCode={}", processId, process.exitValue());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("等待进程中断: pid={}", processId, e);
            }
        }
    }
}