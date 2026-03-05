package com.smartkit.toolbox.entity;

import com.smartkit.toolbox.model.DeviceType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 设备实体类，对应数据库中的device表。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "device")
public class DeviceEntity {

    /**
     * 设备IP地址，主键
     */
    @Id
    @Column(name = "ip")
    private String ip;

    /**
     * 设备名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 设备类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DeviceType type;

    /**
     * 设备型号
     */
    @Column(name = "model")
    private String model;

    /**
     * 设备版本
     */
    @Column(name = "version")
    private String version;

    /**
     * 登录用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 登录密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}