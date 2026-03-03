package com.smartkit.toolbox.controller;

import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.Result;
import com.smartkit.toolbox.model.dto.OperationLogListResult;
import com.smartkit.toolbox.model.dto.OperationLogQueryDTO;
import com.smartkit.toolbox.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/v1/operation-logs")
@Tag(name = "Operation Log", description = "操作日志API")
public class OperationLogController {

    private static final int MAX_LIMIT = 100;

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 批量查询操作日志
     */
    @GetMapping
    @Operation(summary = "批量查询操作日志")
    public Result<OperationLogListResult> getOperationLogs(OperationLogQueryDTO query) {
        Integer offset = query.getOffset();
        Integer limit = query.getLimit();

        // 参数校验
        if (offset == null || offset < 0) {
            offset = 0;
        }
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        if (limit > MAX_LIMIT) {
            return Result.error(400, "单次查询数量不能超过 " + MAX_LIMIT + " 条");
        }

        List<OperationLog> logs = operationLogService.queryLogs(
            offset, limit, query.getKeyword(), query.getStartTime(),
            query.getEndTime(), query.getResult(), query.getSortBy(), query.getSortOrder()
        );

        long total = operationLogService.countLogs(
            query.getKeyword(), query.getStartTime(), query.getEndTime(), query.getResult()
        );

        return Result.success(new OperationLogListResult(logs, total));
    }

    /**
     * 导出操作日志到 Excel
     */
    @GetMapping("/export")
    @Operation(summary = "导出操作日志到Excel")
    public void exportOperationLogs(OperationLogQueryDTO query, HttpServletResponse response) throws IOException {
        operationLogService.exportLogs(
            response, query.getOffset(), query.getLimit(), query.getKeyword(),
            query.getStartTime(), query.getEndTime(), query.getResult()
        );
    }

    /**
     * 获取当前语言环境（预留）
     */
    @GetMapping("/locale")
    @Operation(summary = "获取当前语言环境")
    public Result<String> getLocale() {
        return Result.success(operationLogService.getCurrentLocale());
    }

    /**
     * 设置语言环境（预留）
     */
    @PutMapping("/locale")
    @Operation(summary = "设置语言环境")
    public Result<String> setLocale(@RequestParam String locale) {
        // 预留实现
        return Result.success("语言环境设置功能预留实现");
    }

    /**
     * 不支持删除操作日志
     */
    @DeleteMapping
    @Operation(summary = "不支持删除操作日志")
    public Result<Void> deleteNotAllowed() {
        return Result.error(405, "Method Not Allowed");
    }
}