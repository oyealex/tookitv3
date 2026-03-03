package com.smartkit.toolbox.service;

import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;
import com.smartkit.toolbox.repository.OperationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationLogServiceTest {

    @Mock
    private OperationLogRepository repository;

    @Mock
    private OperationLogCache cache;

    @Mock
    private OperationLogI18nUtil i18nUtil;

    private OperationLogService operationLogService;

    @BeforeEach
    void setUp() {
        operationLogService = new OperationLogService(repository, cache, i18nUtil);
    }

    private OperationLog createTestLog() {
        OperationLog log = new OperationLog();
        log.setOperationType(OperationType.CREATE);
        log.setObjectType("Device");
        log.setObjectId("1");
        log.setObjectName("Test Device");
        log.setDescription("operation.device.created");
        log.setResult(OperationResult.SUCCESS);
        log.setOperator("admin");
        log.setOperatorIp("192.168.1.1");
        return log;
    }

    @Test
    @DisplayName("同步添加操作日志 - 成功")
    void logOperation_Success() {
        OperationLog log = createTestLog();
        when(i18nUtil.getLocalizedMessage(anyString())).thenReturn("设备创建成功");

        operationLogService.logOperation(log);

        verify(repository).insert(any(OperationLog.class));
        assertNotNull(log.getOperationTime());
    }

    @Test
    @DisplayName("异步添加操作日志 - 成功")
    void logOperationAsync_Success() {
        OperationLog log = createTestLog();
        when(i18nUtil.getLocalizedMessage(anyString())).thenReturn("设备创建成功");

        operationLogService.logOperationAsync(log);

        verify(cache).add(any(OperationLog.class));
        assertNotNull(log.getOperationTime());
    }

    @Test
    @DisplayName("批量查询操作日志 - 成功")
    void queryLogs_Success() {
        List<OperationLog> expectedLogs = List.of(createTestLog());
        when(repository.findAll(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
            .thenReturn(expectedLogs);

        List<OperationLog> result = operationLogService.queryLogs(0, 20, null, null, null, null, null, null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(repository).findAll(eq(0), eq(20), isNull(), isNull(), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    @DisplayName("批量查询操作日志 - 分页参数校验")
    void queryLogs_PaginationValidation() {
        // 测试 offset 为负数时默认为0
        when(repository.findAll(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of());

        operationLogService.queryLogs(-1, 20, null, null, null, null, null, null);

        verify(repository).findAll(eq(0), eq(20), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("批量查询操作日志 - limit超过最大值时限制为100")
    void queryLogs_ExceedMaxLimit() {
        when(repository.findAll(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of());

        operationLogService.queryLogs(0, 200, null, null, null, null, null, null);

        verify(repository).findAll(eq(0), eq(100), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("批量查询操作日志 - limit为null时使用默认值20")
    void queryLogs_NullLimit() {
        when(repository.findAll(anyInt(), anyInt(), any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of());

        operationLogService.queryLogs(0, null, null, null, null, null, null, null);

        verify(repository).findAll(eq(0), eq(20), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("统计操作日志数量 - 成功")
    void countLogs_Success() {
        when(repository.count(any(), any(), any(), any())).thenReturn(10L);

        long count = operationLogService.countLogs(null, null, null, null);

        assertEquals(10L, count);
    }

    @Test
    @DisplayName("获取当前语言环境")
    void getCurrentLocale() {
        String locale = operationLogService.getCurrentLocale();

        assertNotNull(locale);
    }
}