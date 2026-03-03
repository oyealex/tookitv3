package com.smartkit.toolbox.model.tool;

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
}