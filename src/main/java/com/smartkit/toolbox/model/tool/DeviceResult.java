package com.smartkit.toolbox.model.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备运维结果 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResult {

    /**
     * 设备 IP 地址
     */
    private String ip;

    /**
     * 运维状态：success-成功、failed-失败、not_executed-未执行
     */
    private String status;

    /**
     * 详细信息
     */
    private String message;

    /**
     * 成功状态常量
     */
    public static final String STATUS_SUCCESS = "success";

    /**
     * 失败状态常量
     */
    public static final String STATUS_FAILED = "failed";

    /**
     * 未执行状态常量
     */
    public static final String STATUS_NOT_EXECUTED = "not_executed";
}