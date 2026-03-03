package com.smartkit.toolbox.repository;

import com.smartkit.toolbox.entity.OperationLogEntity;
import com.smartkit.toolbox.mapper.OperationLogMapper;
import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.OperationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class OperationLogRepository {

    private final OperationLogJpaRepository jpaRepository;
    private final OperationLogMapper mapper;

    public OperationLogRepository(OperationLogJpaRepository jpaRepository, OperationLogMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    public int insert(OperationLog log) {
        OperationLogEntity entity = mapper.toEntity(log);
        LocalDateTime now = LocalDateTime.now();
        if (entity.getOperationTime() == null) {
            entity.setOperationTime(now);
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        jpaRepository.save(entity);
        return 1;
    }

    public int[] batchInsert(List<OperationLog> logs) {
        LocalDateTime now = LocalDateTime.now();
        List<OperationLogEntity> entities = logs.stream()
            .map(log -> {
                OperationLogEntity entity = mapper.toEntity(log);
                if (entity.getOperationTime() == null) {
                    entity.setOperationTime(now);
                }
                if (entity.getCreatedAt() == null) {
                    entity.setCreatedAt(now);
                }
                return entity;
            })
            .toList();
        jpaRepository.saveAll(entities);
        return new int[entities.size()];
    }

    public List<OperationLog> findAll(int offset, int limit, String keyword,
                                       LocalDateTime startTime, LocalDateTime endTime,
                                       OperationResult result, String sortBy, String sortOrder) {
        // 计算页码
        int page = offset / limit;
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder != null ? sortOrder : "DESC"),
            convertSortField(sortBy));
        PageRequest pageRequest = PageRequest.of(page, limit, sort);

        Page<OperationLogEntity> entityPage = jpaRepository.findByConditions(
            keyword, startTime, endTime, result, pageRequest
        );

        return mapper.toModelList(entityPage.getContent());
    }

    private String convertSortField(String sortBy) {
        if (sortBy == null) {
            return "operationTime";
        }
        return switch (sortBy.toLowerCase()) {
            case "objectname" -> "objectName";
            case "operatorip" -> "operatorIp";
            case "description" -> "description";
            default -> "operationTime";
        };
    }

    public long count(String keyword, LocalDateTime startTime, LocalDateTime endTime, OperationResult result) {
        if (keyword == null && startTime == null && endTime == null && result == null) {
            return jpaRepository.count();
        }
        return jpaRepository.countByConditions(keyword, startTime, endTime, result);
    }

    public Optional<OperationLog> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }
}