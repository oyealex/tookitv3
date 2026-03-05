package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 设备创建DTO，用于创建设备时的请求参数封装。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
public class DeviceCreateDTO {
    /**
     * 设备IP地址，必填，需符合IPv4格式
     */
    @NotBlank(message = "{error.device.ip.required}")
    @Pattern(regexp = "^(?:(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$", message = "{error.device.ip.invalid}")
    private String ip;

    /**
     * 设备名称，最大120字符
     */
    @Size(max = 120, message = "{error.device.name.too.long}")
    private String name;

    /**
     * 设备类型，必填
     */
    @NotNull(message = "{error.device.type.required}")
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
     * 登录密码，必填，最大255字符
     */
    @NotBlank(message = "{error.device.password.required}")
    @Size(max = 255, message = "{error.device.password.too.long}")
    private String password;
}