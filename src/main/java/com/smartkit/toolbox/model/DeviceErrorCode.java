package com.smartkit.toolbox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceErrorCode {

    DEVICE_NOT_FOUND(1001, "error.device.not.found"),
    IP_INVALID(1002, "error.device.ip.invalid"),
    IP_DUPLICATE(1003, "error.device.ip.duplicate"),
    DEVICE_LIMIT_EXCEEDED(1004, "error.device.limit.exceeded"),
    BATCH_SIZE_EXCEEDED(1005, "error.device.batch.size.exceeded"),
    NAME_TOO_LONG(1006, "error.device.name.too.long"),
    INVALID_DEVICE_TYPE(1007, "error.device.type.invalid");

    private final int code;
    private final String messageKey;
}