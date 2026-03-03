package com.smartkit.toolbox.monitor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartkit.toolbox.model.tool.ToolResult;
import com.smartkit.toolbox.monitor.ToolExecutionMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 轮询工具执行监控器
 * 通过轮询 result.json 文件检测工具执行完毕
 */
public class PollingToolExecutionMonitor implements ToolExecutionMonitor {

    private static final Logger log = LoggerFactory.getLogger(PollingToolExecutionMonitor.class);

    /**
     * 结果文件名
     */
    private static final String RESULT_FILE_NAME = "result.json";

    /**
     * 轮询间隔（毫秒）
     */
    private static final long POLLING_INTERVAL_MS = 1000;

    private final ObjectMapper objectMapper;
    private volatile String workDir;
    private volatile boolean monitoring = false;
    private ToolResult result;

    public PollingToolExecutionMonitor() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void startMonitoring(String workDir) {
        this.workDir = workDir;
        this.monitoring = true;
        this.result = null;
        log.info("开始监控工具执行: workDir={}", workDir);
    }

    @Override
    public ToolResult waitForCompletion() throws InterruptedException {
        Path resultFile = Paths.get(workDir, RESULT_FILE_NAME);

        while (monitoring) {
            if (Files.exists(resultFile)) {
                try {
                    ToolResult toolResult = objectMapper.readValue(resultFile.toFile(), ToolResult.class);

                    // 检查状态
                    if (ToolResult.STATUS_COMPLETED.equals(toolResult.getStatus())) {
                        this.result = toolResult;
                        this.monitoring = false;
                        log.info("工具执行完成: toolId={}, result={}", toolResult.getToolId(), toolResult.getResult());
                        return toolResult;
                    }

                } catch (IOException e) {
                    log.warn("读取结果文件失败: {}", e.getMessage());
                }
            }

            // 等待下次轮询
            Thread.sleep(POLLING_INTERVAL_MS);
        }

        return result;
    }

    @Override
    public boolean isMonitoring() {
        return monitoring;
    }

    @Override
    public void stopMonitoring() {
        this.monitoring = false;
        log.info("停止监控工具执行");
    }

    /**
     * 获取当前结果
     */
    public ToolResult getResult() {
        return result;
    }
}