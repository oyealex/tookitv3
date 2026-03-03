package com.smartkit.toolbox.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Device {
    private String ip;
    private String name;
    private DeviceType type;
    private String model;
    private String version;
    private String username;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}