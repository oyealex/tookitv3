package com.smartkit.toolbox.mapper;

import com.smartkit.toolbox.entity.DeviceEntity;
import com.smartkit.toolbox.model.Device;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceMapper {

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

    public List<Device> toModelList(List<DeviceEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }

    public List<DeviceEntity> toEntityList(List<Device> devices) {
        return devices.stream()
                .map(this::toEntity)
                .toList();
    }
}