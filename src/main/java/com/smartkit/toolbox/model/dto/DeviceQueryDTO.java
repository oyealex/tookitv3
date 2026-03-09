package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.DeviceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

/**
 * 设备查询DTO，用于查询设备列表时的请求参数封装。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
public class DeviceQueryDTO {
    /**
     * 查询偏移量，默认0
     */
    @Min(value = 0, message = "{error.device.query.offset.invalid}")
    private Integer offset = 0;

    /**
     * 查询数量限制，默认20，最大100
     */
    @Min(value = 1, message = "{error.device.query.limit.min}")
    @Max(value = 100, message = "{error.device.query.limit.exceeded}")
    private Integer limit = 20;

    /**
     * 设备IP地址（模糊匹配）
     */
    private String ip;

    /**
     * 设备名称（模糊匹配）
     */
    private String name;

    /**
     * 设备类型
     */
    private DeviceType type;

    /**
     * 设备型号（模糊匹配）
     */
    private String model;

    /**
     * 设备版本（模糊匹配）
     */
    private String version;

    /**
     * 是否已锁定（null-不限，true-已锁定，false-未锁定）
     */
    private Boolean locked;
}