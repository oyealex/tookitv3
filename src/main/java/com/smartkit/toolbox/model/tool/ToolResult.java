package com.smartkit.toolbox.model.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 子工具执行结果 DTO，对应 result.json
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolResult {

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
     * 结束时间（格式：2026-03-03 23:34:01.123）
     */
    private String endTime;

    /**
     * 执行状态（当前固定为 completed，由工具箱监控检测）
     */
    private String status;

    /**
     * 执行结果：success-成功、failed-失败
     */
    private String result;

    /**
     * 设备运维结果列表
     */
    private List<DeviceResult> deviceResults;

    /**
     * 交付件列表
     */
    private List<Artifact> artifacts;

    /**
     * 状态常量：已完成
     */
    public static final String STATUS_COMPLETED = "completed";

    /**
     * 状态常量：运行中
     */
    public static final String STATUS_RUNNING = "running";

    /**
     * 结果常量：成功
     */
    public static final String RESULT_SUCCESS = "success";

    /**
     * 结果常量：失败
     */
    public static final String RESULT_FAILED = "failed";
}