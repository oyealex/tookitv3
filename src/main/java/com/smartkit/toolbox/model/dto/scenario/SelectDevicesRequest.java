package com.smartkit.toolbox.model.dto.scenario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 选择设备请求 DTO
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectDevicesRequest {
    /**
     * 设备 IP 列表
     */
    private List<String> deviceIps;
}
