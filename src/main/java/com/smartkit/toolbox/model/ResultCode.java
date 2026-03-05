package com.smartkit.toolbox.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用响应状态码枚举。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 请求成功
     */
    SUCCESS(200, "success"),

    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * 资源未找到
     */
    NOT_FOUND(404, "Not Found"),

    /**
     * 服务器内部错误
     */
    INTERNAL_ERROR(500, "Internal Server Error");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态消息
     */
    private final String message;

}