package com.smartkit.toolbox.repository;

import com.smartkit.toolbox.entity.OperationLogEntity;
import com.smartkit.toolbox.model.OperationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogJpaRepository extends JpaRepository<OperationLogEntity, Long> {

    // 分页查询（带筛选条件）
    @Query("SELECT o FROM OperationLogEntity o WHERE " +
           "(:keyword IS NULL OR o.objectName LIKE %:keyword%) AND " +
           "(:startTime IS NULL OR o.operationTime >= :startTime) AND " +
           "(:endTime IS NULL OR o.operationTime <= :endTime) AND " +
           "(:result IS NULL OR o.result = :result)")
    Page<OperationLogEntity> findByConditions(
            @Param("keyword") String keyword,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("result") OperationResult result,
            Pageable pageable);

    // 统计总数（带筛选条件）
    @Query("SELECT COUNT(o) FROM OperationLogEntity o WHERE " +
           "(:keyword IS NULL OR o.objectName LIKE %:keyword%) AND " +
           "(:startTime IS NULL OR o.operationTime >= :startTime) AND " +
           "(:endTime IS NULL OR o.operationTime <= :endTime) AND " +
           "(:result IS NULL OR o.result = :result)")
    long countByConditions(
            @Param("keyword") String keyword,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("result") OperationResult result);
}