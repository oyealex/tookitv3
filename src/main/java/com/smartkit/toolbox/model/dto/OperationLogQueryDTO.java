package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.OperationResult;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志查询参数 DTO
 */
@Data
public class OperationLogQueryDTO {
    private Integer offset = 0;
    private Integer limit = 20;
    private String keyword;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private OperationResult result;
    private String sortBy;
    private String sortOrder;
}