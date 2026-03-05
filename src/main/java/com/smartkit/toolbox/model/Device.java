package com.smartkit.toolbox.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 设备实体类，用于表示系统中的设备对象。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
public class Device {
    /**
     * 设备IP地址，作为唯一标识符
     */
    private String ip;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备类型
     */
    private DeviceType type;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 设备版本
     */
    private String version;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}