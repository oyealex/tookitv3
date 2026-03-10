package com.smartkit.toolbox.model.dto.scenario;

import com.smartkit.toolbox.model.scenario.ScenarioStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 场景执行状态响应 DTO
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioStatusResponse {
    /**
     * 场景 ID
     */
    private String scenarioId;

    /**
     * 场景名称
     */
    private String scenarioName;

    /**
     * 当前状态
     */
    private ScenarioStatus status;

    /**
     * 已选中的设备列表
     */
    private List<String> selectedDevices;

    /**
     * 当前步骤索引
     */
    private Integer currentStepIndex;
}
