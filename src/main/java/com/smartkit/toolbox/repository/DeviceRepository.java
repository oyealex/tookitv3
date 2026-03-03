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

@Repository
public class DeviceRepository {

    private final DeviceJpaRepository jpaRepository;
    private final DeviceMapper mapper;

    public DeviceRepository(DeviceJpaRepository jpaRepository, DeviceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    public void insert(Device device) {
        DeviceEntity entity = mapper.toEntity(device);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        jpaRepository.save(entity);
    }

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

    public Optional<Device> findByIp(String ip) {
        return jpaRepository.findById(ip).map(mapper::toModel);
    }

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

    public long count() {
        return jpaRepository.count();
    }

    public long count(String ip, String name, DeviceType type, String model, String version) {
        if (ip == null && name == null && type == null && model == null && version == null) {
            return jpaRepository.count();
        }
        return jpaRepository.countByConditions(ip, name, type, model, version);
    }

    public void update(Device device) {
        DeviceEntity entity = mapper.toEntity(device);
        entity.setUpdatedAt(LocalDateTime.now());
        // 如果实体已存在，merge会自动更新
        jpaRepository.save(entity);
    }

    public void deleteByIp(String ip) {
        jpaRepository.deleteById(ip);
    }

    @Transactional
    public int[] deleteByIps(List<String> ips) {
        jpaRepository.deleteByIpIn(ips);
        return new int[ips.size()];
    }

    public boolean existsByIp(String ip) {
        return jpaRepository.existsById(ip);
    }
}