package com.smartkit.toolbox.model.tool;

import com.smartkit.toolbox.model.scenario.ScenarioStepExecution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 子工具输入数据 DTO，生成 input.json 文件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolInput {

    /**
     * 子工具 ID
     */
    private String toolId;

    /**
     * 子工具名称
     */
    private String toolName;

    /**
     * 启动时间（格式：2026-03-03 23:34:01.123）
     */
    private String startTime;

    /**
     * 工作目录路径
     */
    private String workDir;

    /**
     * 设备列表
     */
    private List<DeviceInfo> devices;

    /**
     * 场景信息（仅在场景模式下存在）
     */
    private ScenarioInfo scenario;

    /**
     * 前置工具执行结果（仅在场景模式下且不是第一步时存在）
     */
    private PreviousResult previousResult;
}
