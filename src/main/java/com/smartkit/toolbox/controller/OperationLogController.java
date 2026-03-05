package com.smartkit.toolbox.controller;

import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.Result;
import com.smartkit.toolbox.model.dto.OperationLogListResult;
import com.smartkit.toolbox.model.dto.OperationLogQueryDTO;
import com.smartkit.toolbox.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 操作日志控制器，提供操作日志相关的REST API接口。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/operation-logs")
@Tag(name = "Operation Log", description = "操作日志API")
@Validated
public class OperationLogController {

    /**
     * 操作日志服务
     */
    private final OperationLogService operationLogService;

    /**
     * 构造方法，注入依赖的服务
     *
     * @param operationLogService 操作日志服务
     */
    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 批量查询操作日志
     *
     * @param query 查询条件DTO
     * @return 操作日志列表及总数
     */
    @GetMapping
    @Operation(summary = "批量查询操作日志")
    public Result<OperationLogListResult> getOperationLogs(@Valid OperationLogQueryDTO query) {
        // DTO 已经对 offset 和 limit 进行校验
        List<OperationLog> logs = operationLogService.queryLogs(
            query.getOffset(), query.getLimit(), query.getKeyword(), query.getStartTime(),
            query.getEndTime(), query.getResult(), query.getSortBy(), query.getSortOrder()
        );

        long total = operationLogService.countLogs(
            query.getKeyword(), query.getStartTime(), query.getEndTime(), query.getResult()
        );

        return Result.success(new OperationLogListResult(logs, total));
    }

    /**
     * 导出操作日志到 Excel
     *
     * @param query 查询条件
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
    @GetMapping("/export")
    @Operation(summary = "导出操作日志到Excel")
    public void exportOperationLogs(@Valid OperationLogQueryDTO query, HttpServletResponse response) throws IOException {
        operationLogService.exportLogs(
            response, query.getOffset(), query.getLimit(), query.getKeyword(),
            query.getStartTime(), query.getEndTime(), query.getResult()
        );
    }

    /**
     * 获取当前语言环境（预留）
     *
     * @return 当前语言环境
     */
    @GetMapping("/locale")
    @Operation(summary = "获取当前语言环境")
    public Result<String> getLocale() {
        return Result.success(operationLogService.getCurrentLocale());
    }

    /**
     * 设置语言环境（预留）
     *
     * @param locale 语言环境代码
     * @return 操作结果
     */
    @PutMapping("/locale")
    @Operation(summary = "设置语言环境")
    public Result<String> setLocale(@RequestParam @NotBlank(message = "{error.locale.required}") @Size(max = 10, message = "{error.locale.too.long}") String locale) {
        // 预留实现
        return Result.success("语言环境设置功能预留实现");
    }

    /**
     * 不支持删除操作日志
     *
     * @return 方法不允许错误响应
     */
    @DeleteMapping
    @Operation(summary = "不支持删除操作日志")
    public Result<Void> deleteNotAllowed() {
        return Result.error(405, "Method Not Allowed");
    }
}