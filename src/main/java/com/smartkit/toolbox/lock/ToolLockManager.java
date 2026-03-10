package com.smartkit.toolbox.lock;

import com.smartkit.toolbox.service.DeviceLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 工具锁管理器
 * 管理全局运行锁和设备锁
 * 支持独立工具执行和场景执行两种模式的互斥
 */
@Component
public class ToolLockManager {

    private static final Logger log = LoggerFactory.getLogger(ToolLockManager.class);

    /**
     * 全局运行锁（同一时间只能运行一个工具或场景）
     */
    private final ReentrantLock globalLock = new ReentrantLock();

    /**
     * 当前运行的工具信息（独立工具执行模式）
     */
    private volatile RunningToolInfo runningToolInfo;

    /**
     * 当前运行的场景信息（场景执行模式）
     */
    private volatile RunningScenarioInfo runningScenarioInfo;

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
     *
     * @param deviceLockService 设备锁服务
     */
    public void setDeviceLockService(DeviceLockService deviceLockService) {
        this.deviceLockService = deviceLockService;
    }

    /**
     * 尝试获取全局锁（独立工具执行模式）
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

            // 检查是否有场景正在执行
            if (runningScenarioInfo != null) {
                globalLock.unlock();
                log.warn("场景正在执行中，无法启动工具: toolId={}, scenarioId={}", toolId, runningScenarioInfo.scenarioId());
                return LockResult.fail("场景正在执行中，无法启动工具");
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
            log.info("获取锁成功（工具模式）: toolId={}, deviceIps={}", toolId, deviceIps);

            return LockResult.ok();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: toolId={}", toolId, e);
            return LockResult.fail("获取锁被中断");
        }
    }

    /**
     * 尝试获取全局锁（场景执行模式）
     *
     * @param scenarioId 场景 ID
     * @param scenarioName 场景名称
     * @param timeout    超时时间（毫秒）
     * @return 获取结果
     */
    public LockResult tryLockScenario(String scenarioId, String scenarioName, long timeout) {
        try {
            boolean acquired = globalLock.tryLock(timeout, TimeUnit.MILLISECONDS);
            if (!acquired) {
                log.warn("获取全局锁失败: scenarioId={}, timeout={}ms", scenarioId, timeout);
                return LockResult.fail("系统繁忙，无法获取全局锁");
            }

            // 检查是否有工具正在执行
            if (runningToolInfo != null) {
                globalLock.unlock();
                log.warn("工具正在执行中，无法启动场景: scenarioId={}, toolId={}", scenarioId, runningToolInfo.toolId());
                return LockResult.fail("工具正在执行中，无法启动场景");
            }

            // 检查是否有其他场景正在执行
            if (runningScenarioInfo != null) {
                globalLock.unlock();
                log.warn("其他场景正在执行中: scenarioId={}, otherScenarioId={}", scenarioId, runningScenarioInfo.scenarioId());
                return LockResult.fail("其他场景正在执行中");
            }

            runningScenarioInfo = new RunningScenarioInfo(scenarioId, scenarioName);
            log.info("获取锁成功（场景模式）: scenarioId={}, scenarioName={}", scenarioId, scenarioName);

            return LockResult.ok();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取锁被中断: scenarioId={}", scenarioId, e);
            return LockResult.fail("获取锁被中断");
        }
    }

    /**
     * 释放锁（独立工具执行模式）
     */
    public void unlock() {
        // 解锁设备
        if (deviceLockService != null && runningToolInfo != null) {
            List<String> deviceIps = runningToolInfo.deviceIps();
            if (deviceIps != null && !deviceIps.isEmpty()) {
                deviceLockService.unlockDevices(deviceIps);
                log.info("解锁设备: deviceIps={}", deviceIps);
            }
        }

        // 释放全局锁
        if (globalLock.isHeldByCurrentThread()) {
            globalLock.unlock();
            log.info("释放全局锁（工具模式）");
        }

        runningToolInfo = null;
    }

    /**
     * 释放锁（场景执行模式）
     *
     * @param scenarioId 场景 ID
     */
    public void unlockScenario(String scenarioId) {
        // 释放全局锁
        if (globalLock.isHeldByCurrentThread()) {
            globalLock.unlock();
            log.info("释放全局锁（场景模式）: scenarioId={}", scenarioId);
        }

        runningScenarioInfo = null;
    }

    /**
     * 检查是否有工具正在运行
     *
     * @return 是否有工具正在运行
     */
    public boolean isRunning() {
        return runningToolInfo != null || runningScenarioInfo != null;
    }

    /**
     * 检查是否有场景正在执行
     *
     * @return 是否有场景正在执行
     */
    public boolean isScenarioRunning() {
        return runningScenarioInfo != null;
    }

    /**
     * 检查是否有独立工具正在执行
     *
     * @return 是否有独立工具正在执行
     */
    public boolean isToolRunning() {
        return runningToolInfo != null;
    }

    /**
     * 获取当前运行的工具信息
     *
     * @return 运行的工具信息
     */
    public RunningToolInfo getRunningToolInfo() {
        return runningToolInfo;
    }

    /**
     * 获取当前运行的场景信息
     *
     * @return 运行的场景信息
     */
    public RunningScenarioInfo getRunningScenarioInfo() {
        return runningScenarioInfo;
    }

    /**
     * 锁结果
     *
     * @param success 是否成功
     * @param message 消息
     */
    public record LockResult(boolean success, String message) {
        /**
         * 创建成功结果
         *
         * @return 成功结果
         */
        public static LockResult ok() {
            return new LockResult(true, null);
        }

        /**
         * 创建失败结果
         *
         * @param message 失败消息
         * @return 失败结果
         */
        public static LockResult fail(String message) {
            return new LockResult(false, message);
        }
    }

    /**
     * 运行的工具信息
     *
     * @param toolId    工具 ID
     * @param deviceIps 设备 IP 列表
     */
    public record RunningToolInfo(String toolId, List<String> deviceIps) {
    }

    /**
     * 运行的场景信息
     *
     * @param scenarioId   场景 ID
     * @param scenarioName 场景名称
     */
    public record RunningScenarioInfo(String scenarioId, String scenarioName) {
    }
}