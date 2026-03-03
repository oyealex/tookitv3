package com.smartkit.toolbox.service;

import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 工具操作日志记录器
 */
public class ToolOperationLogger {

    private static final Logger log = LoggerFactory.getLogger(ToolOperationLogger.class);

    /**
     * 记录工具扫描开始
     */
    public static void logScanStart(String toolsDir) {
        String description = String.format("开始扫描工具目录: %s", toolsDir);
        OperationLogHelper.logAsync(
                OperationType.SCAN,
                "Tool",
                null,
                "ToolScanner",
                description,
                OperationResult.SUCCESS
        );
        log.info(description);
    }

    /**
     * 记录工具扫描完成
     */
    public static void logScanComplete(int toolCount) {
        String description = String.format("工具扫描完成，共发现 %d 个工具", toolCount);
        OperationLogHelper.logAsync(
                OperationType.SCAN,
                "Tool",
                null,
                "ToolScanner",
                description,
                OperationResult.SUCCESS
        );
        log.info(description);
    }

    /**
     * 记录工具扫描跳过无效目录
     */
    public static void logScanSkip(String toolId, String reason) {
        String description = String.format("跳过工具 [%s]: %s", toolId, reason);
        OperationLogHelper.logAsync(
                OperationType.SCAN,
                "Tool",
                toolId,
                toolId,
                description,
                OperationResult.SUCCESS
        );
        log.warn(description);
    }

    /**
     * 记录工具启动
     */
    public static void logExecuteStart(String toolId, String toolName, List<String> deviceIps, String workDir) {
        String description = String.format("启动工具 [%s] %s, 设备: %s, 工作目录: %s",
                toolId, toolName, deviceIps, workDir);
        OperationLogHelper.logAsync(
                OperationType.EXECUTE,
                "Tool",
                toolId,
                toolName,
                description,
                OperationResult.SUCCESS
        );
        log.info(description);
    }

    /**
     * 记录工具执行完成
     */
    public static void logExecuteComplete(String toolId, String toolName, String result, long durationMs) {
        String description = String.format("工具 [%s] %s 执行完成, 结果: %s, 耗时: %dms",
                toolId, toolName, result, durationMs);
        OperationLogHelper.logAsync(
                OperationType.EXECUTE,
                "Tool",
                toolId,
                toolName,
                description,
                "success".equals(result) ? OperationResult.SUCCESS : OperationResult.FAILURE
        );
        log.info(description);
    }

    /**
     * 记录工具执行失败
     *
     * @return 总是返回 false（用于便捷调用）
     */
    public static boolean logExecuteFailure(String toolId, String toolName, String errorMessage) {
        String description = String.format("工具 [%s] %s 执行失败: %s",
                toolId, toolName, errorMessage);
        OperationLogHelper.logAsync(
                OperationType.EXECUTE,
                "Tool",
                toolId,
                toolName,
                description,
                OperationResult.FAILURE
        );
        log.error(description);
        return false;
    }
}