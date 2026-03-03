package com.smartkit.toolbox.service;

import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;

import java.time.LocalDateTime;

/**
 * 操作日志静态工具类
 * 提供便捷的静态方法供其他模块调用
 */
public class OperationLogHelper {

    private static OperationLogService operationLogService;

    /**
     * 注入 Service（由 Spring 管理）
     */
    public OperationLogHelper(OperationLogService operationLogService) {
        OperationLogHelper.operationLogService = operationLogService;
    }

    /**
     * 同步记录操作日志
     */
    public static void log(OperationType operationType, String objectType, String objectId,
                           String objectName, String description, OperationResult result) {
        log(operationType, objectType, objectId, objectName, null, description, result, null, null);
    }

    /**
     * 同步记录操作日志（带额外信息）
     */
    public static void log(OperationType operationType, String objectType, String objectId,
                           String objectName, String objectExtra, String description,
                           OperationResult result, String operator, String operatorIp) {
        if (operationLogService == null) {
            return;
        }

        OperationLog log = new OperationLog();
        log.setOperationType(operationType);
        log.setObjectType(objectType);
        log.setObjectId(objectId);
        log.setObjectName(objectName);
        log.setObjectExtra(objectExtra);
        log.setDescription(description);
        log.setResult(result);
        log.setOperator(operator);
        log.setOperatorIp(operatorIp);
        log.setOperationTime(LocalDateTime.now());

        operationLogService.logOperation(log);
    }

    /**
     * 异步记录操作日志
     */
    public static void logAsync(OperationType operationType, String objectType, String objectId,
                                String objectName, String description, OperationResult result) {
        logAsync(operationType, objectType, objectId, objectName, null, description, result, null, null);
    }

    /**
     * 异步记录操作日志（带额外信息）
     */
    public static void logAsync(OperationType operationType, String objectType, String objectId,
                                String objectName, String objectExtra, String description,
                                OperationResult result, String operator, String operatorIp) {
        if (operationLogService == null) {
            return;
        }

        OperationLog log = new OperationLog();
        log.setOperationType(operationType);
        log.setObjectType(objectType);
        log.setObjectId(objectId);
        log.setObjectName(objectName);
        log.setObjectExtra(objectExtra);
        log.setDescription(description);
        log.setResult(result);
        log.setOperator(operator);
        log.setOperatorIp(operatorIp);
        log.setOperationTime(LocalDateTime.now());

        operationLogService.logOperationAsync(log);
    }

    /**
     * 记录失败操作（带失败原因）
     */
    public static void logFailure(OperationType operationType, String objectType, String objectId,
                                  String objectName, String description, String failureReason) {
        log(operationType, objectType, objectId, objectName, description, OperationResult.FAILURE);
    }

    /**
     * 异步记录失败操作
     */
    public static void logFailureAsync(OperationType operationType, String objectType, String objectId,
                                       String objectName, String description, String failureReason) {
        logAsync(operationType, objectType, objectId, objectName, description, OperationResult.FAILURE);
    }
}