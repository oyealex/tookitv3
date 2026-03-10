package com.smartkit.toolbox.model.dto.scenario;

import com.smartkit.toolbox.model.scenario.ScenarioStep;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 场景详情响应 DTO
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioDetailResponse {
    /**
     * 场景 ID
     */
    private String id;

    /**
     * 场景名称
     */
    private String name;

    /**
     * 场景描述
     */
    private String description;

    /**
     * 步骤列表
     */
    private List<ScenarioStep> steps;

    /**
     * 是否允许执行失败时跳过
     */
    private Boolean allowSkipOnFailure;

    /**
     * 是否允许跳过未执行的步骤
     */
    private Boolean allowSkipStep;
}
