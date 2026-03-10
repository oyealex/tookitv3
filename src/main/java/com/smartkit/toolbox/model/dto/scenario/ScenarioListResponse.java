package com.smartkit.toolbox.model.dto.scenario;

import com.smartkit.toolbox.model.scenario.ScenarioStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 场景列表响应 DTO
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScenarioListResponse {
    /**
     * 场景列表
     */
    private List<ScenarioSummary> scenarios;

    /**
     * 场景摘要
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScenarioSummary {
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
         * 步骤数量
         */
        private int stepCount;
    }
}
