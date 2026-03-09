package com.smartkit.toolbox.service;

import com.smartkit.toolbox.model.DeviceLockInfo;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备锁服务接口
 * 由设备管理模块实现，提供设备锁定功能
 */
public interface DeviceLockService {

    /**
     * 锁定设备列表
     *
     * @param ips 设备 IP 列表
     * @throws LockException 如果设备已被锁定或不存在
     */
    void lockDevices(List<String> ips) throws LockException;

    /**
     * 锁定设备列表（带锁定来源）
     *
     * @param ips       设备 IP 列表
     * @param lockSource 锁定来源（如场景 ID）
     * @throws LockException 如果设备已被锁定或不存在
     */
    void lockDevices(List<String> ips, String lockSource) throws LockException;

    /**
     * 解锁设备列表
     *
     * @param ips 设备 IP 列表
     */
    void unlockDevices(List<String> ips);

    /**
     * 按锁定来源解锁设备
     *
     * @param lockSource 锁定来源
     */
    void unlockBySource(String lockSource);

    /**
     * 检查设备是否被锁定
     *
     * @param ip 设备 IP
     * @return true-已锁定，false-未锁定
     */
    boolean isLocked(String ip);

    /**
     * 获取设备锁定信息
     *
     * @param ip 设备 IP
     * @return 锁定信息，未锁定时返回 null
     */
    DeviceLockInfo getLockInfo(String ip);

    /**
     * 锁定异常
     */
    class LockException extends Exception {
        public LockException(String message) {
            super(message);
        }
    }
}