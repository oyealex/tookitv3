package com.smartkit.toolbox.mapper;

import com.smartkit.toolbox.entity.OperationLogEntity;
import com.smartkit.toolbox.model.OperationLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationLogMapper {

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

    public List<OperationLog> toModelList(List<OperationLogEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }

    public List<OperationLogEntity> toEntityList(List<OperationLog> logs) {
        return logs.stream()
                .map(this::toEntity)
                .toList();
    }
}