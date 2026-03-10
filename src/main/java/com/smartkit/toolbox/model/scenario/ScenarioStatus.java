package com.smartkit.toolbox.model.scenario;

/**
 * 场景状态枚举
 * 定义场景执行过程中的各种状态
 *
 * @author SmartKit
 * @since 1.0.0
 */
public enum ScenarioStatus {

    /**
     * 空闲狀態，场景未启动
     */
    IDLE,

    /**
     * 设备选择阶段
     */
    DEVICE_SELECTION,

    /**
     * 场景执行中
     */
    RUNNING,

    /**
     * 场景已完成
     */
    COMPLETED,

    /**
     * 场景执行失败
     */
    FAILED
}
