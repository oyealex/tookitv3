package com.smartkit.toolbox.aspect;

import com.smartkit.toolbox.annotation.LogOperation;
import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;
import com.smartkit.toolbox.service.OperationLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OperationLogAspect 单元测试
 */
@ExtendWith(MockitoExtension.class)
class OperationLogAspectTest {

    private OperationLogAspect operationLogAspect;

    @BeforeEach
    void setUp() {
        // 这里只需要 OperationLogService 来实例化 Aspect
        // 但测试的是 extractLogInfo 方法，不依赖 service
        operationLogAspect = new OperationLogAspect(null);
    }

    @Test
    @DisplayName("extractLogInfo 应设置 operationTime")
    void extractLogInfo_ShouldSetOperationTime() throws Exception {
        // Given
        // 创建一个假的 LogOperation 注解
        LogOperation logOperation = createFakeLogOperation();

        Map<String, Object> variables = new HashMap<>();
        variables.put("result", OperationResult.SUCCESS);

        // When
        // 通过反射调用私有方法 extractLogInfo
        Method method = OperationLogAspect.class.getDeclaredMethod("extractLogInfo", LogOperation.class, Map.class);
        method.setAccessible(true);
        OperationLog result = (OperationLog) method.invoke(operationLogAspect, logOperation, variables);

        // Then
        assertNotNull(result.getOperationTime(), "operationTime should be set");
    }

    /**
     * 创建模拟的 LogOperation 注解
     */
    private LogOperation createFakeLogOperation() {
        return new LogOperation() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return LogOperation.class;
            }

            @Override
            public OperationType operationType() {
                return OperationType.CREATE;
            }

            @Override
            public String objectType() {
                return "Device";
            }

            @Override
            public String objectId() {
                return "";
            }

            @Override
            public String objectName() {
                return "";
            }

            @Override
            public String objectExtra() {
                return "";
            }

            @Override
            public String description() {
                return "";
            }

            @Override
            public String result() {
                return "";
            }

            @Override
            public String failureReason() {
                return "";
            }

            @Override
            public String operator() {
                return "";
            }

            @Override
            public boolean operatorIp() {
                return false;
            }

            @Override
            public boolean logOnSuccess() {
                return true;
            }

            @Override
            public boolean logOnException() {
                return true;
            }

            @Override
            public boolean async() {
                return false;
            }
        };
    }

    @Test
    @DisplayName("extractLogInfo 设置的 operationTime 应接近当前时间")
    void extractLogInfo_OperationTimeShouldBeCurrent() throws Exception {
        // Given
        LogOperation logOperation = createFakeLogOperation();
        LocalDateTime before = LocalDateTime.now();

        Map<String, Object> variables = new HashMap<>();

        // When
        Method method = OperationLogAspect.class.getDeclaredMethod("extractLogInfo", LogOperation.class, Map.class);
        method.setAccessible(true);
        OperationLog result = (OperationLog) method.invoke(operationLogAspect, logOperation, variables);

        LocalDateTime after = LocalDateTime.now();

        // Then
        assertNotNull(result.getOperationTime());
        assertFalse(result.getOperationTime().isBefore(before), "operationTime should not be before test start");
        assertFalse(result.getOperationTime().isAfter(after), "operationTime should not be after test end");
    }
}