package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.DeviceType;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DeviceUpdateDTO {
    @Size(max = 120, message = "{error.device.name.too.long}")
    private String name;

    private DeviceType type;

    @Size(max = 100, message = "{error.device.model.too.long}")
    private String model;

    @Size(max = 50, message = "{error.device.version.too.long}")
    private String version;

    @Size(max = 100, message = "{error.device.username.too.long}")
    private String username;

    @Size(max = 255, message = "{error.device.password.too.long}")
    private String password;
}