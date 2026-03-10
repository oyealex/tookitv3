package com.smartkit.toolbox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 场景相关错误码枚举，定义场景操作的业务错误码。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ScenarioErrorCode {

    /**
     * 场景不存在
     */
    SCENARIO_NOT_FOUND(2001, "error.scenario.not.found"),

    /**
     * 场景执行实例不存在
     */
    SCENARIO_EXECUTION_NOT_FOUND(2002, "error.scenario.execution.not.found"),

    /**
     * 场景已被锁定
     */
    SCENARIO_LOCKED(2003, "error.scenario.locked"),

    /**
     * 场景已结束
     */
    SCENARIO_ALREADY_END(2004, "error.scenario.already.end"),

    /**
     * 场景不允许跳过子工具
     */
    SCENARIO_NOT_ALLOW_SKIP(2005, "error.scenario.not.allow.skip"),

    /**
     * 设备不存在
     */
    DEVICE_NOT_FOUND(2006, "error.device.not.found"),

    /**
     * 设备已被锁定
     */
    DEVICE_LOCKED(2007, "error.device.locked"),

    /**
     * 无效的步骤索引
     */
    INVALID_STEP_INDEX(2008, "error.scenario.invalid.step.index"),

    /**
     * 步骤已执行
     */
    STEP_ALREADY_EXECUTED(2009, "error.scenario.step.already.executed");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 国际化消息key
     */
    private final String messageKey;
}
