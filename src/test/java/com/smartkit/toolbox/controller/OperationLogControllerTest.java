package com.smartkit.toolbox.controller;

import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.dto.OperationLogListResult;
import com.smartkit.toolbox.repository.DeviceRepository;
import com.smartkit.toolbox.repository.OperationLogRepository;
import com.smartkit.toolbox.service.OperationLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperationLogController.class)
class OperationLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OperationLogService operationLogService;

    @MockBean
    private DeviceRepository deviceRepository;

    @MockBean
    private OperationLogRepository operationLogRepository;

    private OperationLog testLog;

    @BeforeEach
    void setUp() {
        testLog = new OperationLog();
        testLog.setId(1L);
        testLog.setOperationType(com.smartkit.toolbox.model.OperationType.CREATE);
        testLog.setObjectType("Device");
        testLog.setObjectId("1");
        testLog.setObjectName("Test Device");
        testLog.setDescription("设备创建成功");
        testLog.setResult(OperationResult.SUCCESS);
        testLog.setOperator("admin");
    }

    @Test
    @DisplayName("GET /api/v1/operation-logs - 查询成功")
    void getOperationLogs_Success() throws Exception {
        OperationLogListResult result = new OperationLogListResult(List.of(testLog), 1L);
        when(operationLogService.queryLogs(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of(testLog));
        when(operationLogService.countLogs(any(), any(), any(), any())).thenReturn(1L);

        mockMvc.perform(get("/api/v1/operation-logs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.list").isArray())
            .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/operation-logs - limit超过最大值")
    void getOperationLogs_ExceedMaxLimit() throws Exception {
        mockMvc.perform(get("/api/v1/operation-logs")
                .param("limit", "200"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @DisplayName("GET /api/v1/operation-logs - 使用分页参数")
    void getOperationLogs_WithPagination() throws Exception {
        when(operationLogService.queryLogs(eq(10), eq(20), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of(testLog));
        when(operationLogService.countLogs(any(), any(), any(), any())).thenReturn(1L);

        mockMvc.perform(get("/api/v1/operation-logs")
                .param("offset", "10")
                .param("limit", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("GET /api/v1/operation-logs/export - 导出成功")
    void exportOperationLogs_Success() throws Exception {
        when(operationLogService.queryLogs(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of(testLog));

        mockMvc.perform(get("/api/v1/operation-logs/export"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/operation-logs/locale - 获取语言环境")
    void getLocale_Success() throws Exception {
        when(operationLogService.getCurrentLocale()).thenReturn("zh_CN");

        mockMvc.perform(get("/api/v1/operation-logs/locale"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value("zh_CN"));
    }

    @Test
    @DisplayName("PUT /api/v1/operation-logs/locale - 设置语言环境")
    void setLocale_Success() throws Exception {
        mockMvc.perform(put("/api/v1/operation-logs/locale")
                .param("locale", "en_US"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PUT /api/v1/operation-logs/locale - 缺少locale参数")
    void setLocale_MissingLocale() throws Exception {
        mockMvc.perform(put("/api/v1/operation-logs/locale"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/v1/operation-logs - 不支持删除")
    void deleteNotAllowed() throws Exception {
        mockMvc.perform(delete("/api/v1/operation-logs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(405));
    }
}