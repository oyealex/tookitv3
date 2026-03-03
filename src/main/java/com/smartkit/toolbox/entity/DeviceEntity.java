package com.smartkit.toolbox.entity;

import com.smartkit.toolbox.model.DeviceType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device")
public class DeviceEntity {

    @Id
    @Column(name = "ip")
    private String ip;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private DeviceType type;

    @Column(name = "model")
    private String model;

    @Column(name = "version")
    private String version;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}