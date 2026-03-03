package com.smartkit.toolbox.service;

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
     * 解锁设备列表
     *
     * @param ips 设备 IP 列表
     */
    void unlockDevices(List<String> ips);

    /**
     * 检查设备是否被锁定
     *
     * @param ip 设备 IP
     * @return true-已锁定，false-未锁定
     */
    boolean isLocked(String ip);

    /**
     * 锁定异常
     */
    class LockException extends Exception {
        public LockException(String message) {
            super(message);
        }
    }
}