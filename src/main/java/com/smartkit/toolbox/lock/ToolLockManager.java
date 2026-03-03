package com.smartkit.toolbox.lock;

import com.smartkit.toolbox.service.DeviceLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 工具锁管理器
 * 管理全局运行锁和设备锁
 */
@Component
public class ToolLockManager {

    private static final Logger log = LoggerFactory.getLogger(ToolLockManager.class);

    /**
     * 全局运行锁（同一时间只能运行一个工具）
     */
    private final ReentrantLock globalLock = new ReentrantLock();

    /**
     * 当前运行的工具信息
     */
    private volatile RunningToolInfo runningToolInfo;

    /**
     * 设备锁服务（由设备管理模块提供）
     */
    private DeviceLockService deviceLockService;

    public ToolLockManager() {
    }

    public ToolLockManager(DeviceLockService deviceLockService) {
        this.deviceLockService = deviceLockService;
    }

    /**
     * 设置设备锁服务
     */
    public void setDeviceLockService(DeviceLockService deviceLockService) {
        this.deviceLockService = deviceLockService;
    }

    /**
     * 尝试获取全局锁
     *
     * @param toolId    工具 ID
     * @param deviceIps 设备 IP 列表
     * @param timeout   超时时间（毫秒）
     * @return 获取结果
     */
    public LockResult tryLock(String toolId, List<String> deviceIps, long timeout) {
        try {
            boolean acquired = globalLock.tryLock(timeout, TimeUnit.MILLISECONDS);
            if (!acquired) {
                log.warn("获取全局锁失败: toolId={}, timeout={}ms", toolId, timeout);
                return LockResult.fail("工具正在运行中，无法获取全局锁");
            }

            // 锁定设备
            if (deviceLockService != null && deviceIps != null && !deviceIps.isEmpty()) {
                try {
                    deviceLockService.lockDevices(deviceIps);
                } catch (DeviceLockService.LockException e) {
                    globalLock.unlock();
                    log.warn("锁定设备失败: toolId={}, error={}", toolId, e.getMessage());
                    return LockResult.fail("设备已被锁定: " + e.getMessage());
                }
            }

            runningToolInfo = new RunningToolInfo(toolId, deviceIps);
            log.info("获取锁成功: toolId={}, deviceIps={}", toolId, deviceIps);

            return LockResult.ok();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: toolId={}", toolId, e);
            return LockResult.fail("获取锁被中断");
        }
    }

    /**
     * 释放锁
     */
    public void unlock() {
        // 解锁设备
        if (deviceLockService != null && runningToolInfo != null) {
            List<String> deviceIps = runningToolInfo.deviceIps;
            if (deviceIps != null && !deviceIps.isEmpty()) {
                deviceLockService.unlockDevices(deviceIps);
                log.info("解锁设备: deviceIps={}", deviceIps);
            }
        }

        // 释放全局锁
        if (globalLock.isHeldByCurrentThread()) {
            globalLock.unlock();
            log.info("释放全局锁");
        }

        runningToolInfo = null;
    }

    /**
     * 检查是否有工具正在运行
     */
    public boolean isRunning() {
        return runningToolInfo != null;
    }

    /**
     * 获取当前运行的工具信息
     */
    public RunningToolInfo getRunningToolInfo() {
        return runningToolInfo;
    }

    /**
     * 锁结果
     */
    public record LockResult(boolean success, String message) {
        public static LockResult ok() {
            return new LockResult(true, null);
        }

        public static LockResult fail(String message) {
            return new LockResult(false, message);
        }
    }

    /**
     * 运行的工具信息
     */
    public record RunningToolInfo(String toolId, List<String> deviceIps) {
    }
}