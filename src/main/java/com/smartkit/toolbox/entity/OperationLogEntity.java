package com.smartkit.toolbox.entity;

import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 操作日志实体类，对应数据库中的operation_log表。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "operation_log")
public class OperationLogEntity {

    /**
     * 主键，自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 操作时间
     */
    @Column(name = "operation_time")
    private LocalDateTime operationTime;

    /**
     * 操作类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private OperationType operationType;

    /**
     * 操作对象类型
     */
    @Column(name = "object_type")
    private String objectType;

    /**
     * 操作对象标识
     */
    @Column(name = "object_id")
    private String objectId;

    /**
     * 操作对象名称
     */
    @Column(name = "object_name")
    private String objectName;

    /**
     * 操作对象额外信息
     */
    @Column(name = "object_extra")
    private String objectExtra;

    /**
     * 操作描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 操作结果
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private OperationResult result;

    /**
     * 失败原因
     */
    @Column(name = "failure_reason")
    private String failureReason;

    /**
     * 操作人
     */
    @Column(name = "operator")
    private String operator;

    /**
     * 操作者IP地址
     */
    @Column(name = "operator_ip")
    private String operatorIp;

    /**
     * 记录创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}