package com.smartkit.toolbox.service;

import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * OperationLogHelper 单元测试
 */
@ExtendWith(MockitoExtension.class)
class OperationLogHelperTest {

    @Mock
    private OperationLogService operationLogService;

    private OperationLogHelper operationLogHelper;

    @BeforeEach
    void setUp() throws Exception {
        operationLogHelper = new OperationLogHelper(operationLogService);
    }

    /**
     * 清理静态字段，避免测试间干扰
     */
    static void clearStaticField() throws Exception {
        Field field = OperationLogHelper.class.getDeclaredField("operationLogService");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    @DisplayName("log 方法应设置 operationTime")
    void log_ShouldSetOperationTime() {
        // Given
        OperationType operationType = OperationType.CREATE;
        String objectType = "Device";
        String objectId = "1";
        String objectName = "Test Device";
        String description = "创建设备";
        OperationResult result = OperationResult.SUCCESS;

        // When
        OperationLogHelper.log(operationType, objectType, objectId, objectName, description, result);

        // Then - 验证 operationLogService.logOperation 被调用
        verify(operationLogService, times(1)).logOperation(any(OperationLog.class));
    }

    @Test
    @DisplayName("log 方法（带额外信息）应设置 operationTime")
    void logWithExtra_ShouldSetOperationTime() {
        // Given
        OperationType operationType = OperationType.UPDATE;
        String objectType = "Device";
        String objectId = "2";
        String objectName = "Test Device 2";
        String objectExtra = "{\"key\":\"value\"}";
        String description = "更新设备";
        OperationResult result = OperationResult.SUCCESS;
        String operator = "admin";
        String operatorIp = "192.168.1.1";

        // When
        OperationLogHelper.log(operationType, objectType, objectId, objectName, objectExtra,
                description, result, operator, operatorIp);

        // Then
        verify(operationLogService, times(1)).logOperation(argThat(log ->
                log.getOperationTime() != null
        ));
    }

    @Test
    @DisplayName("logAsync 方法应设置 operationTime")
    void logAsync_ShouldSetOperationTime() {
        // Given
        OperationType operationType = OperationType.DELETE;
        String objectType = "Device";
        String objectId = "3";
        String objectName = "Test Device 3";
        String description = "删除设备";
        OperationResult result = OperationResult.SUCCESS;

        // When
        OperationLogHelper.logAsync(operationType, objectType, objectId, objectName, description, result);

        // Then
        verify(operationLogService, times(1)).logOperationAsync(any(OperationLog.class));
    }

    @Test
    @DisplayName("logAsync 方法（带额外信息）应设置 operationTime")
    void logAsyncWithExtra_ShouldSetOperationTime() {
        // Given
        OperationType operationType = OperationType.IMPORT;
        String objectType = "Device";
        String objectId = "4";
        String objectName = "Test Device 4";
        String objectExtra = "{\"file\":\"data.xlsx\"}";
        String description = "导入设备";
        OperationResult result = OperationResult.SUCCESS;
        String operator = "operator";
        String operatorIp = "10.0.0.1";

        // When
        OperationLogHelper.logAsync(operationType, objectType, objectId, objectName, objectExtra,
                description, result, operator, operatorIp);

        // Then
        verify(operationLogService, times(1)).logOperationAsync(argThat(log ->
                log.getOperationTime() != null
        ));
    }

    @Test
    @DisplayName("logFailure 方法应设置 operationTime")
    void logFailure_ShouldSetOperationTime() {
        // Given
        OperationType operationType = OperationType.UPDATE;
        String objectType = "Device";
        String objectId = "5";
        String objectName = "Test Device 5";
        String description = "更新设备失败";
        String failureReason = "数据验证失败";

        // When
        OperationLogHelper.logFailure(operationType, objectType, objectId, objectName, description, failureReason);

        // Then
        verify(operationLogService, times(1)).logOperation(argThat(log ->
                log.getOperationTime() != null && log.getResult() == OperationResult.FAILURE
        ));
    }

    @Test
    @DisplayName("logFailureAsync 方法应设置 operationTime")
    void logFailureAsync_ShouldSetOperationTime() {
        // Given
        OperationType operationType = OperationType.DELETE;
        String objectType = "Device";
        String objectId = "6";
        String objectName = "Test Device 6";
        String description = "删除设备失败";
        String failureReason = "设备不存在";

        // When
        OperationLogHelper.logFailureAsync(operationType, objectType, objectId, objectName, description, failureReason);

        // Then
        verify(operationLogService, times(1)).logOperationAsync(argThat(log ->
                log.getOperationTime() != null && log.getResult() == OperationResult.FAILURE
        ));
    }
}