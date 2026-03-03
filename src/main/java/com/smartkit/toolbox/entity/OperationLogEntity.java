package com.smartkit.toolbox.entity;

import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_log")
public class OperationLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "operation_time")
    private LocalDateTime operationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private OperationType operationType;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "object_id")
    private String objectId;

    @Column(name = "object_name")
    private String objectName;

    @Column(name = "object_extra")
    private String objectExtra;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private OperationResult result;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "operator")
    private String operator;

    @Column(name = "operator_ip")
    private String operatorIp;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}