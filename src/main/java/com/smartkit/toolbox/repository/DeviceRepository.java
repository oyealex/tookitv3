package com.smartkit.toolbox.repository;

import com.smartkit.toolbox.entity.DeviceEntity;
import com.smartkit.toolbox.mapper.DeviceMapper;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.DeviceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 设备仓库类，提供设备的持久化操作。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Repository
public class DeviceRepository {

    /**
     * JPA仓库
     */
    private final DeviceJpaRepository jpaRepository;

    /**
     * 对象映射器
     */
    private final DeviceMapper mapper;

    /**
     * 构造方法，注入依赖
     *
     * @param jpaRepository JPA仓库
     * @param mapper 对象映射器
     */
    public DeviceRepository(DeviceJpaRepository jpaRepository, DeviceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * 插入设备
     *
     * @param device 设备对象
     */
    public void insert(Device device) {
        DeviceEntity entity = mapper.toEntity(device);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        jpaRepository.save(entity);
    }

    /**
     * 批量插入设备
     *
     * @param devices 设备列表
     * @return 插入结果数组
     */
    @Transactional
    public int[] batchInsert(List<Device> devices) {
        List<DeviceEntity> entities = devices.stream()
            .map(d -> {
                DeviceEntity entity = mapper.toEntity(d);
                entity.setCreatedAt(LocalDateTime.now());
                entity.setUpdatedAt(LocalDateTime.now());
                return entity;
            })
            .toList();
        jpaRepository.saveAll(entities);
        // 返回与原有接口兼容的int数组
        return new int[entities.size()];
    }

    /**
     * 根据IP地址查询设备
     *
     * @param ip 设备IP地址
     * @return 设备Optional
     */
    public Optional<Device> findByIp(String ip) {
        return jpaRepository.findById(ip).map(mapper::toModel);
    }

    /**
     * 分页查询设备列表
     *
     * @param offset 查询偏移量
     * @param limit 查询数量限制
     * @param ip 设备IP过滤
     * @param name 设备名称过滤
     * @param type 设备类型过滤
     * @param model 设备型号过滤
     * @param version 设备版本过滤
     * @return 设备列表
     */
    public List<Device> findAll(int offset, int limit, String ip, String name, DeviceType type, String model, String version) {
        // 简化处理：使用分页查询，然后手动处理筛选条件
        // 由于JPA的分页和原生SQL动态条件的差异，这里需要调整实现
        // 使用PageRequest进行分页，offset转为page
        int page = offset / limit;
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<DeviceEntity> entityPage = jpaRepository.findByConditions(
            ip, name, type, model, version, pageRequest
        );

        // JPA的Page包含所有符合条件的数据，这里需要转换
        // 注意：JPA的Page实现与原有逻辑略有不同
        return mapper.toModelList(entityPage.getContent());
    }

    /**
     * 统计设备总数
     *
     * @return 设备总数
     */
    public long count() {
        return jpaRepository.count();
    }

    /**
     * 根据条件统计设备数量
     *
     * @param ip 设备IP过滤
     * @param name 设备名称过滤
     * @param type 设备类型过滤
     * @param model 设备型号过滤
     * @param version 设备版本过滤
     * @return 设备数量
     */
    public long count(String ip, String name, DeviceType type, String model, String version) {
        if (ip == null && name == null && type == null && model == null && version == null) {
            return jpaRepository.count();
        }
        return jpaRepository.countByConditions(ip, name, type, model, version);
    }

    /**
     * 更新设备
     *
     * @param device 设备对象
     */
    public void update(Device device) {
        DeviceEntity entity = mapper.toEntity(device);
        entity.setUpdatedAt(LocalDateTime.now());
        // 如果实体已存在，merge会自动更新
        jpaRepository.save(entity);
    }

    /**
     * 根据IP删除设备
     *
     * @param ip 设备IP地址
     */
    public void deleteByIp(String ip) {
        jpaRepository.deleteById(ip);
    }

    /**
     * 批量删除设备
     *
     * @param ips 设备IP地址列表
     * @return 删除结果数组
     */
    public int[] deleteByIps(List<String> ips) {
        jpaRepository.deleteByIpIn(ips);
        return new int[ips.size()];
    }

    /**
     * 删除所有设备
     */
    @Transactional
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    /**
     * 检查设备是否存在
     *
     * @param ip 设备IP地址
     * @return 是否存在
     */
    public boolean existsByIp(String ip) {
        return jpaRepository.existsById(ip);
    }
}