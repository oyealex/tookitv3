package com.smartkit.toolbox.mapper;

import com.smartkit.toolbox.entity.OperationLogEntity;
import com.smartkit.toolbox.model.OperationLog;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 操作日志对象映射器，负责OperationLog模型与OperationLogEntity实体之间的相互转换。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Component
public class OperationLogMapper {

    /**
     * 将实体对象转换为模型对象
     *
     * @param entity 操作日志实体
     * @return 操作日志模型
     */
    public OperationLog toModel(OperationLogEntity entity) {
        if (entity == null) {
            return null;
        }
        OperationLog log = new OperationLog();
        log.setId(entity.getId());
        log.setOperationTime(entity.getOperationTime());
        log.setOperationType(entity.getOperationType());
        log.setObjectType(entity.getObjectType());
        log.setObjectId(entity.getObjectId());
        log.setObjectName(entity.getObjectName());
        log.setObjectExtra(entity.getObjectExtra());
        log.setDescription(entity.getDescription());
        log.setResult(entity.getResult());
        log.setFailureReason(entity.getFailureReason());
        log.setOperator(entity.getOperator());
        log.setOperatorIp(entity.getOperatorIp());
        log.setCreatedAt(entity.getCreatedAt());
        return log;
    }

    /**
     * 将模型对象转换为实体对象
     *
     * @param log 操作日志模型
     * @return 操作日志实体
     */
    public OperationLogEntity toEntity(OperationLog log) {
        if (log == null) {
            return null;
        }
        OperationLogEntity entity = new OperationLogEntity();
        entity.setId(log.getId());
        entity.setOperationTime(log.getOperationTime());
        entity.setOperationType(log.getOperationType());
        entity.setObjectType(log.getObjectType());
        entity.setObjectId(log.getObjectId());
        entity.setObjectName(log.getObjectName());
        entity.setObjectExtra(log.getObjectExtra());
        entity.setDescription(log.getDescription());
        entity.setResult(log.getResult());
        entity.setFailureReason(log.getFailureReason());
        entity.setOperator(log.getOperator());
        entity.setOperatorIp(log.getOperatorIp());
        entity.setCreatedAt(log.getCreatedAt());
        return entity;
    }

    /**
     * 将实体列表转换为模型列表
     *
     * @param entities 操作日志实体列表
     * @return 操作日志模型列表
     */
    public List<OperationLog> toModelList(List<OperationLogEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }

    /**
     * 将模型列表转换为实体列表
     *
     * @param logs 操作日志模型列表
     * @return 操作日志实体列表
     */
    public List<OperationLogEntity> toEntityList(List<OperationLog> logs) {
        return logs.stream()
                .map(this::toEntity)
                .toList();
    }
}