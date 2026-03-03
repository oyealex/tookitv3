package com.smartkit.toolbox.monitor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartkit.toolbox.model.tool.ToolResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PollingToolExecutionMonitor 单元测试
 */
class PollingToolExecutionMonitorTest {

    @TempDir
    Path tempDir;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testStartMonitoring() {
        PollingToolExecutionMonitor monitor = new PollingToolExecutionMonitor();

        monitor.startMonitoring(tempDir.toString());

        assertTrue(monitor.isMonitoring());
        monitor.stopMonitoring();
    }

    @Test
    void testStopMonitoring() {
        PollingToolExecutionMonitor monitor = new PollingToolExecutionMonitor();

        monitor.startMonitoring(tempDir.toString());
        assertTrue(monitor.isMonitoring());

        monitor.stopMonitoring();
        assertFalse(monitor.isMonitoring());
    }

    @Test
    void testWaitForCompletionWithResultFile() throws Exception {
        // 创建 result.json 文件
        ToolResult result = ToolResult.builder()
                .toolId("device-ping")
                .toolName("设备Ping工具")
                .status(ToolResult.STATUS_COMPLETED)
                .result(ToolResult.RESULT_SUCCESS)
                .build();

        Path resultFile = tempDir.resolve("result.json");
        objectMapper.writeValue(resultFile.toFile(), result);

        // 启动监控
        PollingToolExecutionMonitor monitor = new PollingToolExecutionMonitor();
        monitor.startMonitoring(tempDir.toString());

        // 等待完成
        ToolResult waitResult = monitor.waitForCompletion();

        assertNotNull(waitResult);
        assertEquals(ToolResult.STATUS_COMPLETED, waitResult.getStatus());
        assertEquals(ToolResult.RESULT_SUCCESS, waitResult.getResult());
    }

    @Test
    void testGetResult() throws Exception {
        // 创建 result.json 文件
        ToolResult result = ToolResult.builder()
                .toolId("device-ping")
                .status(ToolResult.STATUS_COMPLETED)
                .result(ToolResult.RESULT_SUCCESS)
                .build();

        Path resultFile = tempDir.resolve("result.json");
        objectMapper.writeValue(resultFile.toFile(), result);

        PollingToolExecutionMonitor monitor = new PollingToolExecutionMonitor();
        monitor.startMonitoring(tempDir.toString());

        // 等待完成
        monitor.waitForCompletion();

        // 获取结果
        ToolResult cachedResult = monitor.getResult();
        assertNotNull(cachedResult);
        assertEquals("device-ping", cachedResult.getToolId());
    }
}