package com.smartkit.toolbox.model.dto.scenario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 终止场景响应 DTO
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminateScenarioResponse {
    /**
     * 场景 ID
     */
    private String scenarioId;

    /**
     * 新状态
     */
    private String status;
}
