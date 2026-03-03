package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.OperationLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 操作日志列表返回结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationLogListResult {
    private List<OperationLog> list;
    private long total;
}