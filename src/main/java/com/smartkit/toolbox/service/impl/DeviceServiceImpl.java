package com.smartkit.toolbox.service.impl;

import com.smartkit.toolbox.common.BusinessException;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.DeviceErrorCode;
import com.smartkit.toolbox.model.DeviceType;
import com.smartkit.toolbox.model.dto.BatchResultDTO;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.model.dto.DeviceQueryDTO;
import com.smartkit.toolbox.model.dto.DeviceUpdateDTO;
import com.smartkit.toolbox.repository.DeviceRepository;
import com.smartkit.toolbox.service.DeviceService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备服务实现类，实现设备管理的具体业务逻辑。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    /**
     * 最大设备数量限制
     */
    private static final int MAX_DEVICES = 1000;

    /**
     * 批量操作最大数量限制
     */
    private static final int MAX_BATCH_SIZE = 100;

    /**
     * 最大失败原因记录数
     */
    private static final int MAX_FAIL_REASONS = 10;

    /**
     * 设备仓库
     */
    private final DeviceRepository deviceRepository;

    /**
     * 消息源，用于国际化
     */
    private final MessageSource messageSource;

    /**
     * 构造方法，注入依赖的服务
     *
     * @param deviceRepository 设备仓库
     * @param messageSource 消息源
     */
    public DeviceServiceImpl(DeviceRepository deviceRepository, MessageSource messageSource) {
        this.deviceRepository = deviceRepository;
        this.messageSource = messageSource;
    }

    /**
     * 获取国际化消息的辅助方法
     *
     * @param key 消息key
     * @param args 消息参数
     * @return 国际化消息
     */
    private String msg(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    @Override
    public Device addDevice(DeviceCreateDTO dto) {
        validateDeviceCreate(dto);
        checkDeviceLimit(1);

        Device device = new Device();
        device.setIp(dto.getIp());
        device.setName(resolveName(dto.getName(), dto.getType(), dto.getIp()));
        device.setType(dto.getType());
        device.setModel(dto.getModel());
        device.setVersion(dto.getVersion());
        device.setUsername(dto.getUsername());
        device.setPassword(dto.getPassword());

        deviceRepository.insert(device);
        return deviceRepository.findByIp(dto.getIp()).orElseThrow();
    }

    @Override
    @Transactional
    public BatchResultDTO addDevices(List<DeviceCreateDTO> devices) {
        if (devices.size() > MAX_BATCH_SIZE) {
            throw new BusinessException(DeviceErrorCode.BATCH_SIZE_EXCEEDED.getCode(),
                msg("error.device.batch.add.exceeded", MAX_BATCH_SIZE));
        }

        int successCount = 0;
        List<BatchResultDTO.FailReason> failReasons = new ArrayList<>();
        long currentCount = deviceRepository.count();

        for (int i = 0; i < devices.size(); i++) {
            DeviceCreateDTO dto = devices.get(i);
            try {
                if (currentCount + successCount >= MAX_DEVICES) {
                    failReasons.add(new BatchResultDTO.FailReason(i + 1, dto.getIp(),
                        msg("error.device.limit.exceeded")));
                    continue;
                }

                validateDeviceCreate(dto);

                Device device = new Device();
                device.setIp(dto.getIp());
                device.setName(resolveName(dto.getName(), dto.getType(), dto.getIp()));
                device.setType(dto.getType());
                device.setModel(dto.getModel());
                device.setVersion(dto.getVersion());
                device.setUsername(dto.getUsername());
                device.setPassword(dto.getPassword());

                deviceRepository.insert(device);
                successCount++;
            } catch (BusinessException e) {
                if (failReasons.size() < MAX_FAIL_REASONS) {
                    failReasons.add(new BatchResultDTO.FailReason(i + 1, dto.getIp(), e.getMessage()));
                }
            }
        }

        return new BatchResultDTO(successCount, devices.size() - successCount, failReasons);
    }

    @Override
    public Device getDevice(String ip) {
        return deviceRepository.findByIp(ip)
            .orElseThrow(() -> new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND.getCode(),
                msg("error.device.not.found")));
    }

    @Override
    public List<Device> getDevices(DeviceQueryDTO query) {
        if (query.getLimit() > MAX_BATCH_SIZE) {
            throw new BusinessException(DeviceErrorCode.BATCH_SIZE_EXCEEDED.getCode(),
                msg("error.device.query.limit.exceeded", MAX_BATCH_SIZE));
        }
        return deviceRepository.findAll(
            query.getOffset(),
            query.getLimit(),
            query.getIp(),
            query.getName(),
            query.getType(),
            query.getModel(),
            query.getVersion()
        );
    }

    @Override
    public long count() {
        return deviceRepository.count();
    }

    @Override
    public long count(DeviceQueryDTO query) {
        return deviceRepository.count(
            query.getIp(),
            query.getName(),
            query.getType(),
            query.getModel(),
            query.getVersion()
        );
    }

    @Override
    public Device updateDevice(String ip, DeviceUpdateDTO dto) {
        Device existing = getDevice(ip);

        if (dto.getName() != null) {
            existing.setName(dto.getName());
        }
        if (dto.getType() != null) {
            existing.setType(dto.getType());
        }
        if (dto.getModel() != null) {
            existing.setModel(dto.getModel());
        }
        if (dto.getVersion() != null) {
            existing.setVersion(dto.getVersion());
        }
        if (dto.getUsername() != null) {
            existing.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            existing.setPassword(dto.getPassword());
        }

        deviceRepository.update(existing);
        return deviceRepository.findByIp(ip).orElseThrow();
    }

    @Override
    public void deleteDevice(String ip) {
        if (!deviceRepository.existsByIp(ip)) {
            throw new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND.getCode(),
                msg("error.device.not.found"));
        }
        deviceRepository.deleteByIp(ip);
    }

    @Override
    @Transactional
    public BatchResultDTO deleteDevices(List<String> ips) {
        if (ips.size() > MAX_BATCH_SIZE) {
            throw new BusinessException(DeviceErrorCode.BATCH_SIZE_EXCEEDED.getCode(),
                msg("error.device.batch.delete.exceeded", MAX_BATCH_SIZE));
        }

        int successCount = 0;
        List<BatchResultDTO.FailReason> failReasons = new ArrayList<>();

        for (int i = 0; i < ips.size(); i++) {
            String ip = ips.get(i);
            if (deviceRepository.existsByIp(ip)) {
                deviceRepository.deleteByIp(ip);
                successCount++;
            } else {
                if (failReasons.size() < MAX_FAIL_REASONS) {
                    failReasons.add(new BatchResultDTO.FailReason(i + 1, ip,
                        msg("error.device.not.found")));
                }
            }
        }

        return new BatchResultDTO(successCount, ips.size() - successCount, failReasons);
    }

    /**
     * 验证设备创建参数
     *
     * @param dto 设备创建DTO
     */
    private void validateDeviceCreate(DeviceCreateDTO dto) {
        // IP 格式校验由 DTO 的 @Pattern 注解处理
        // 此处仅检查 IP 是否重复（业务规则校验）
        if (deviceRepository.existsByIp(dto.getIp())) {
            throw new BusinessException(DeviceErrorCode.IP_DUPLICATE.getCode(),
                msg("error.device.ip.duplicate"));
        }
    }

    /**
     * 检查设备数量限制
     *
     * @param additional 新增设备数量
     */
    private void checkDeviceLimit(int additional) {
        long currentCount = deviceRepository.count();
        if (currentCount + additional > MAX_DEVICES) {
            throw new BusinessException(DeviceErrorCode.DEVICE_LIMIT_EXCEEDED.getCode(),
                msg("error.device.limit.exceeded"));
        }
    }

    /**
     * 解析设备名称，如果为空则使用类型+IP的默认格式
     *
     * @param name 设备名称
     * @param type 设备类型
     * @param ip 设备IP
     * @return 设备名称
     */
    private String resolveName(String name, DeviceType type, String ip) {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return type.name() + "-" + ip;
    }
}