package com.smartkit.toolbox.repository;

import com.smartkit.toolbox.entity.DeviceEntity;
import com.smartkit.toolbox.model.DeviceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceJpaRepository extends JpaRepository<DeviceEntity, String> {

    // 根据IP查询设备
    DeviceEntity findByIp(String ip);

    // 检查设备是否存在
    boolean existsByIp(String ip);

    // 分页查询（带筛选条件）- 原有的动态SQL查询逻辑
    @Query("SELECT d FROM DeviceEntity d WHERE " +
           "(:ip IS NULL OR d.ip LIKE %:ip%) AND " +
           "(:name IS NULL OR d.name LIKE %:name%) AND " +
           "(:type IS NULL OR d.type = :type) AND " +
           "(:model IS NULL OR d.model LIKE %:model%) AND " +
           "(:version IS NULL OR d.version LIKE %:version%)")
    Page<DeviceEntity> findByConditions(
            @Param("ip") String ip,
            @Param("name") String name,
            @Param("type") DeviceType type,
            @Param("model") String model,
            @Param("version") String version,
            Pageable pageable);

    // 统计总数（带筛选条件）
    @Query("SELECT COUNT(d) FROM DeviceEntity d WHERE " +
           "(:ip IS NULL OR d.ip LIKE %:ip%) AND " +
           "(:name IS NULL OR d.name LIKE %:name%) AND " +
           "(:type IS NULL OR d.type = :type) AND " +
           "(:model IS NULL OR d.model LIKE %:model%) AND " +
           "(:version IS NULL OR d.version LIKE %:version%)")
    long countByConditions(
            @Param("ip") String ip,
            @Param("name") String name,
            @Param("type") DeviceType type,
            @Param("model") String model,
            @Param("version") String version);

    // 批量删除
    void deleteByIpIn(List<String> ips);
}