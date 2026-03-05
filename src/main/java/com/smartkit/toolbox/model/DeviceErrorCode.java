package com.smartkit.toolbox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备相关错误码枚举，定义设备操作的业务错误码。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DeviceErrorCode {

    /**
     * 设备不存在
     */
    DEVICE_NOT_FOUND(1001, "error.device.not.found"),

    /**
     * IP地址无效
     */
    IP_INVALID(1002, "error.device.ip.invalid"),

    /**
     * IP地址重复
     */
    IP_DUPLICATE(1003, "error.device.ip.duplicate"),

    /**
     * 设备数量超过限制
     */
    DEVICE_LIMIT_EXCEEDED(1004, "error.device.limit.exceeded"),

    /**
     * 批量操作数量超限
     */
    BATCH_SIZE_EXCEEDED(1005, "error.device.batch.size.exceeded"),

    /**
     * 设备名称过长
     */
    NAME_TOO_LONG(1006, "error.device.name.too.long"),

    /**
     * 设备类型无效
     */
    INVALID_DEVICE_TYPE(1007, "error.device.type.invalid");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 国际化消息key
     */
    private final String messageKey;
}