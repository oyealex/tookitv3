package com.smartkit.toolbox.model;

import java.time.LocalDateTime;

/**
 * 设备锁定信息模型
 * 记录设备的锁定状态和来源信息
 *
 * @author SmartKit
 * @since 1.0.0
 */
public class DeviceLockInfo {

    /**
     * 设备 IP 地址
     */
    private String ip;

    /**
     * 锁定来源（如场景 ID）
     */
    private String lockedBy;

    /**
     * 锁定时间
     */
    private LocalDateTime lockedAt;

    /**
     * 构造方法
     */
    public DeviceLockInfo() {
    }

    /**
     * 构造方法
     *
     * @param ip 设备 IP
     * @param lockedBy 锁定来源
     * @param lockedAt 锁定时间
     */
    public DeviceLockInfo(String ip, String lockedBy, LocalDateTime lockedAt) {
        this.ip = ip;
        this.lockedBy = lockedBy;
        this.lockedAt = lockedAt;
    }

    /**
     * 获取设备 IP
     *
     * @return 设备 IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置设备 IP
     *
     * @param ip 设备 IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取锁定来源
     *
     * @return 锁定来源
     */
    public String getLockedBy() {
        return lockedBy;
    }

    /**
     * 设置锁定来源
     *
     * @param lockedBy 锁定来源
     */
    public void setLockedBy(String lockedBy) {
        this.lockedBy = lockedBy;
    }

    /**
     * 获取锁定时间
     *
     * @return 锁定时间
     */
    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    /**
     * 设置锁定时间
     *
     * @param lockedAt 锁定时间
     */
    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }
}
