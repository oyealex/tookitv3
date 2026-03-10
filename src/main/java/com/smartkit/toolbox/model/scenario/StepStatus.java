package com.smartkit.toolbox.model.scenario;

/**
 * 场景步骤状态枚举
 * 定义场景中每个步骤的执行状态
 *
 * @author SmartKit
 * @since 1.0.0
 */
public enum StepStatus {

    /**
     * 待执行
     */
    PENDING,

    /**
     * 执行中
     */
    RUNNING,

    /**
     * 已完成
     */
    COMPLETED,

    /**
     * 执行失败
     */
    FAILED,

    /**
     * 已跳过
     */
    SKIPPED
}
