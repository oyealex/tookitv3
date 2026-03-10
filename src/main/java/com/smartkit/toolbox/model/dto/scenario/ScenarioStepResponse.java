package com.smartkit.toolbox.model.dto.scenario;

import com.smartkit.toolbox.model.scenario.StepStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 场景步骤信息响应 DTO
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioStepResponse {
    /**
     * 步骤索引
     */
    private int stepIndex;

    /**
     * 工具 ID
     */
    private String toolId;

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 执行状态
     */
    private StepStatus status;

    /**
     * 开始时间（时间戳）
     */
    private Long startTime;

    /**
     * 结束时间（时间戳）
     */
    private Long endTime;
}
