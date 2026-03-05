package com.smartkit.toolbox.mapper;

import com.smartkit.toolbox.entity.DeviceEntity;
import com.smartkit.toolbox.model.Device;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备对象映射器，负责Device模型与DeviceEntity实体之间的相互转换。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Component
public class DeviceMapper {

    /**
     * 将实体对象转换为模型对象
     *
     * @param entity 设备实体
     * @return 设备模型
     */
    public Device toModel(DeviceEntity entity) {
        if (entity == null) {
            return null;
        }
        Device device = new Device();
        device.setIp(entity.getIp());
        device.setName(entity.getName());
        device.setType(entity.getType());
        device.setModel(entity.getModel());
        device.setVersion(entity.getVersion());
        device.setUsername(entity.getUsername());
        device.setPassword(entity.getPassword());
        device.setCreatedAt(entity.getCreatedAt());
        device.setUpdatedAt(entity.getUpdatedAt());
        return device;
    }

    /**
     * 将模型对象转换为实体对象
     *
     * @param device 设备模型
     * @return 设备实体
     */
    public DeviceEntity toEntity(Device device) {
        if (device == null) {
            return null;
        }
        DeviceEntity entity = new DeviceEntity();
        entity.setIp(device.getIp());
        entity.setName(device.getName());
        entity.setType(device.getType());
        entity.setModel(device.getModel());
        entity.setVersion(device.getVersion());
        entity.setUsername(device.getUsername());
        entity.setPassword(device.getPassword());
        entity.setCreatedAt(device.getCreatedAt());
        entity.setUpdatedAt(device.getUpdatedAt());
        return entity;
    }

    /**
     * 将实体列表转换为模型列表
     *
     * @param entities 设备实体列表
     * @return 设备模型列表
     */
    public List<Device> toModelList(List<DeviceEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }

    /**
     * 将模型列表转换为实体列表
     *
     * @param devices 设备模型列表
     * @return 设备实体列表
     */
    public List<DeviceEntity> toEntityList(List<Device> devices) {
        return devices.stream()
                .map(this::toEntity)
                .toList();
    }
}