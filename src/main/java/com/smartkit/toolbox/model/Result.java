package com.smartkit.toolbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果类，用于所有REST API的响应封装。
 *
 * @param <T> 响应数据的泛型类型
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 创建成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T> 泛型类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 创建成功响应（无数据）
     *
     * @param <T> 泛型类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 创建错误响应（使用ResultCode枚举）
     *
     * @param resultCode 错误码枚举
     * @param <T> 泛型类型
     * @return 错误响应结果
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 创建错误响应（自定义错误码和消息）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 泛型类型
     * @return 错误响应结果
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

}