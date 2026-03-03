package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.DeviceType;
import lombok.Data;

@Data
public class DeviceQueryDTO {
    private Integer offset = 0;
    private Integer limit = 20;
    private String ip;
    private String name;
    private DeviceType type;
    private String model;
    private String version;
}