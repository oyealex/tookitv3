package com.smartkit.toolbox.common;

import com.smartkit.toolbox.model.ResultCode;
import lombok.Getter;

/**
 * 业务异常类，用于封装业务逻辑错误。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 构造方法，使用ResultCode枚举
     *
     * @param resultCode 响应码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造方法，使用自定义错误码和消息
     *
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

}