package com.smartkit.toolbox.model.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 场景信息
 * 传递给工具的场景上下文信息
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioInfo {

    /**
     * 场景 ID
     */
    private String scenarioId;

    /**
     * 场景名称
     */
    private String scenarioName;

    /**
     * 当前步骤索引（0-based）
     */
    private Integer stepIndex;
}
