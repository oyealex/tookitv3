package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.OperationResult;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志查询参数DTO，用于查询操作日志列表时的请求参数封装。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
public class OperationLogQueryDTO {
    /**
     * 查询偏移量，默认0
     */
    @Min(value = 0, message = "{error.device.query.offset.invalid}")
    private Integer offset = 0;

    /**
     * 查询数量限制，默认20，最大100
     */
    @Min(value = 1, message = "{error.device.query.limit.min}")
    @Max(value = 100, message = "{error.operationlog.query.limit.exceeded}")
    private Integer limit = 20;

    /**
     * 关键字搜索，最大100字符
     */
    @Size(max = 100, message = "{error.device.query.keyword.too.long}")
    private String keyword;

    /**
     * 操作开始时间
     */
    private LocalDateTime startTime;

    /**
     * 操作结束时间
     */
    private LocalDateTime endTime;

    /**
     * 操作结果过滤
     */
    private OperationResult result;

    /**
     * 排序字段
     */
    private String sortBy;

    /**
     * 排序方向（ASC/DESC）
     */
    private String sortOrder;
}