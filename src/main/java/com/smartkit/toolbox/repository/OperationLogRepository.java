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

/**
 * 操作日志仓库类，提供操作日志的持久化操作。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Repository
public class OperationLogRepository {

    /**
     * JPA仓库
     */
    private final OperationLogJpaRepository jpaRepository;

    /**
     * 对象映射器
     */
    private final OperationLogMapper mapper;

    /**
     * 构造方法，注入依赖
     *
     * @param jpaRepository JPA仓库
     * @param mapper 对象映射器
     */
    public OperationLogRepository(OperationLogJpaRepository jpaRepository, OperationLogMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * 插入单条操作日志
     *
     * @param log 操作日志对象
     * @return 插入的记录数
     */
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

    /**
     * 批量插入操作日志
     *
     * @param logs 操作日志列表
     * @return 插入结果数组
     */
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
                // objectId 为必填字段，如果为 null 则设置为空字符串
                if (entity.getObjectId() == null) {
                    entity.setObjectId("");
                }
                return entity;
            })
            .toList();
        jpaRepository.saveAll(entities);
        return new int[entities.size()];
    }

    /**
     * 分页查询操作日志
     *
     * @param offset 查询偏移量
     * @param limit 查询数量限制
     * @param keyword 关键字搜索
     * @param startTime 操作开始时间
     * @param endTime 操作结束时间
     * @param result 操作结果过滤
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 操作日志列表
     */
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

    /**
     * 转换排序字段名称
     *
     * @param sortBy 原始排序字段
     * @return 转换后的排序字段
     */
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

    /**
     * 统计操作日志数量
     *
     * @param keyword 关键字搜索
     * @param startTime 操作开始时间
     * @param endTime 操作结束时间
     * @param result 操作结果过滤
     * @return 日志总数
     */
    public long count(String keyword, LocalDateTime startTime, LocalDateTime endTime, OperationResult result) {
        if (keyword == null && startTime == null && endTime == null && result == null) {
            return jpaRepository.count();
        }
        return jpaRepository.countByConditions(keyword, startTime, endTime, result);
    }

    /**
     * 根据ID查询操作日志
     *
     * @param id 日志ID
     * @return 操作日志 Optional
     */
    public Optional<OperationLog> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toModel);
    }
}