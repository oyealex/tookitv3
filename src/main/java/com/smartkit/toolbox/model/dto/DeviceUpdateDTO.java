package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.DeviceType;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 设备更新DTO，用于更新设备信息时的请求参数封装。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
public class DeviceUpdateDTO {
    /**
     * 设备名称，最大120字符
     */
    @Size(max = 120, message = "{error.device.name.too.long}")
    private String name;

    /**
     * 设备类型
     */
    private DeviceType type;

    /**
     * 设备型号，最大100字符
     */
    @Size(max = 100, message = "{error.device.model.too.long}")
    private String model;

    /**
     * 设备版本，最大50字符
     */
    @Size(max = 50, message = "{error.device.version.too.long}")
    private String version;

    /**
     * 登录用户名，最大100字符
     */
    @Size(max = 100, message = "{error.device.username.too.long}")
    private String username;

    /**
     * 登录密码，最大255字符
     */
    @Size(max = 255, message = "{error.device.password.too.long}")
    private String password;
}