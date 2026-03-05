package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.OperationLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 操作日志列表返回结果DTO，用于封装查询结果。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationLogListResult {
    /**
     * 操作日志列表
     */
    private List<OperationLog> list;

    /**
     * 总记录数
     */
    private long total;
}