package com.smartkit.toolbox.model.scenario;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 场景步骤模型
 * 定义场景中的单个步骤，对应一个子工具
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioStep {

    /**
     * 工具 ID
     */
    private String toolId;

    /**
     * 步骤名称（可选）
     */
    private String name;
}
