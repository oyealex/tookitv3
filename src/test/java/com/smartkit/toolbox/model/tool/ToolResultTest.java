package com.smartkit.toolbox.model.tool;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ToolResult 单元测试
 */
class ToolResultTest {

    @Test
    void testConstants() {
        assertEquals("completed", ToolResult.STATUS_COMPLETED);
        assertEquals("running", ToolResult.STATUS_RUNNING);
        assertEquals("success", ToolResult.RESULT_SUCCESS);
        assertEquals("failed", ToolResult.RESULT_FAILED);
    }

    @Test
    void testBuilder() {
        DeviceResult deviceResult = DeviceResult.builder()
                .ip("192.168.1.1")
                .status(DeviceResult.STATUS_SUCCESS)
                .message("ping成功")
                .build();

        Artifact artifact = Artifact.builder()
                .name("report.pdf")
                .path("report.pdf")
                .size(1024L)
                .build();

        ToolResult result = ToolResult.builder()
                .toolId("device-ping")
                .toolName("设备Ping工具")
                .startTime("2026-03-03 23:34:01.123")
                .endTime("2026-03-03 23:34:05.456")
                .status(ToolResult.STATUS_COMPLETED)
                .result(ToolResult.RESULT_SUCCESS)
                .deviceResults(List.of(deviceResult))
                .artifacts(List.of(artifact))
                .build();

        assertEquals("device-ping", result.getToolId());
        assertEquals("设备Ping工具", result.getToolName());
        assertEquals(ToolResult.STATUS_COMPLETED, result.getStatus());
        assertEquals(ToolResult.RESULT_SUCCESS, result.getResult());
        assertEquals(1, result.getDeviceResults().size());
        assertEquals(1, result.getArtifacts().size());
    }

    @Test
    void testDeviceResultConstants() {
        assertEquals("success", DeviceResult.STATUS_SUCCESS);
        assertEquals("failed", DeviceResult.STATUS_FAILED);
        assertEquals("not_executed", DeviceResult.STATUS_NOT_EXECUTED);
    }
}