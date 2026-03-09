package com.smartkit.toolbox.service.impl;

import com.smartkit.toolbox.common.BusinessException;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.DeviceErrorCode;
import com.smartkit.toolbox.model.DeviceLockInfo;
import com.smartkit.toolbox.model.DeviceType;
import com.smartkit.toolbox.model.dto.BatchResultDTO;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.model.dto.DeviceQueryDTO;
import com.smartkit.toolbox.model.dto.DeviceUpdateDTO;
import com.smartkit.toolbox.repository.DeviceRepository;
import com.smartkit.toolbox.service.DeviceLockService;
import com.smartkit.toolbox.service.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备服务实现类，实现设备管理的具体业务逻辑。
 * 同时实现 DeviceLockService 接口，提供设备锁定功能。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Service
public class DeviceServiceImpl implements DeviceService, DeviceLockService {

    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);

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
     * 设备锁定状态管理（IP -> DeviceLockInfo）
     */
    private final Map<String, DeviceLockInfo> deviceLocks = new ConcurrentHashMap<>();

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

        List<Device> devices = deviceRepository.findAll(
            query.getOffset(),
            query.getLimit(),
            query.getIp(),
            query.getName(),
            query.getType(),
            query.getModel(),
            query.getVersion()
        );

        // 如果指定了locked条件，在内存中进行过滤
        if (query.getLocked() != null) {
            devices = devices.stream()
                .filter(d -> isLocked(d.getIp()) == query.getLocked())
                .toList();
        }

        return devices;
    }

    @Override
    public long count() {
        return deviceRepository.count();
    }

    @Override
    public long count(DeviceQueryDTO query) {
        List<Device> devices = getDevices(query);
        return devices.size();
    }

    @Override
    public Device updateDevice(String ip, DeviceUpdateDTO dto) {
        checkDeviceLock(ip);
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

    /**
     * 检查设备是否被锁定
     *
     * @param ip 设备 IP
     * @throws BusinessException 如果设备被锁定
     */
    private void checkDeviceLock(String ip) {
        DeviceLockInfo lockInfo = deviceLocks.get(ip);
        if (lockInfo != null) {
            String lockSource = lockInfo.getLockedBy() != null ? lockInfo.getLockedBy() : "unknown";
            throw new BusinessException(DeviceErrorCode.DEVICE_LOCKED.getCode(),
                msg("error.device.locked", lockSource));
        }
    }

    @Override
    public void deleteDevice(String ip) {
        checkDeviceLock(ip);
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
            try {
                checkDeviceLock(ip);
                if (deviceRepository.existsByIp(ip)) {
                    deviceRepository.deleteByIp(ip);
                    successCount++;
                } else {
                    if (failReasons.size() < MAX_FAIL_REASONS) {
                        failReasons.add(new BatchResultDTO.FailReason(i + 1, ip,
                            msg("error.device.not.found")));
                    }
                }
            } catch (BusinessException e) {
                if (failReasons.size() < MAX_FAIL_REASONS) {
                    failReasons.add(new BatchResultDTO.FailReason(i + 1, ip, e.getMessage()));
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

    // ==================== DeviceLockService 接口实现 ====================

    @Override
    public void lockDevices(List<String> ips) throws LockException {
        lockDevices(ips, null);
    }

    @Override
    public void lockDevices(List<String> ips, String lockSource) throws LockException {
        List<String> alreadyLocked = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        for (String ip : ips) {
            Device device = deviceRepository.findByIp(ip).orElse(null);
            if (device == null) {
                notFound.add(ip);
            } else if (deviceLocks.containsKey(ip)) {
                alreadyLocked.add(ip);
            }
        }

        if (!notFound.isEmpty()) {
            throw new LockException("设备不存在: " + String.join(", ", notFound));
        }

        if (!alreadyLocked.isEmpty()) {
            throw new LockException("设备已被锁定: " + String.join(", ", alreadyLocked));
        }

        // 所有设备通过验证，执行锁定
        LocalDateTime now = LocalDateTime.now();
        for (String ip : ips) {
            DeviceLockInfo lockInfo = new DeviceLockInfo(ip, lockSource, now);
            deviceLocks.put(ip, lockInfo);
            log.info("锁定设备: ip={}, lockSource={}", ip, lockSource);
        }
    }

    @Override
    public void unlockDevices(List<String> ips) {
        for (String ip : ips) {
            deviceLocks.remove(ip);
            log.info("解锁设备: ip={}", ip);
        }
    }

    @Override
    public void unlockBySource(String lockSource) {
        if (lockSource == null) {
            return;
        }

        deviceLocks.entrySet().removeIf(entry -> {
            if (lockSource.equals(entry.getValue().getLockedBy())) {
                log.info("按来源解锁设备: ip={}, lockSource={}", entry.getKey(), lockSource);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean isLocked(String ip) {
        return deviceLocks.containsKey(ip);
    }

    @Override
    public DeviceLockInfo getLockInfo(String ip) {
        return deviceLocks.get(ip);
    }
}