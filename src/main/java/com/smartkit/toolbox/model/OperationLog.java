package com.smartkit.toolbox.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
public class OperationLog {
    /**
     * 主键，自增
     */
    private Long id;

    /**
     * 操作时间，精确到毫秒
     */
    private LocalDateTime operationTime;

    /**
     * 操作类型
     */
    private OperationType operationType;

    /**
     * 操作对象类型（如 DEVICE）
     */
    private String objectType;

    /**
     * 操作对象标识（如设备 IP）
     */
    private String objectId;

    /**
     * 操作对象名称/关键字，用于关键字搜索
     */
    private String objectName;

    /**
     * 操作对象额外信息，JSON 格式
     */
    private String objectExtra;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作结果
     */
    private OperationResult result;

    /**
     * 失败原因，仅在结果为 FAILURE 时有值
     */
    private String failureReason;

    /**
     * 操作人（可选）
     */
    private String operator;

    /**
     * 操作者 IP 地址
     */
    private String operatorIp;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdAt;
}