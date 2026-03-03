package com.smartkit.toolbox.scanner;

import com.smartkit.toolbox.model.tool.ToolConfig;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 工具扫描器接口
 */
public interface ToolScanner {

    /**
     * 扫描工具目录，发现所有可用的子工具
     *
     * @return 工具配置列表
     * @throws IOException 如果扫描过程中发生 IO 错误
     */
    List<ToolConfig> scan() throws IOException;

    /**
     * 根据工具 ID 获取工具配置
     *
     * @param toolId 工具 ID
     * @return 工具配置（如果存在）
     */
    Optional<ToolConfig> getTool(String toolId);
}