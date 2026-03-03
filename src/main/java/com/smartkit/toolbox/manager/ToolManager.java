package com.smartkit.toolbox.manager;

import com.smartkit.toolbox.model.tool.ToolConfig;
import com.smartkit.toolbox.scanner.ToolScanner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具管理器，管理已发现的子工具
 */
@Service
public class ToolManager {

    private final ToolScanner scanner;
    private final Map<String, ToolConfig> tools = new ConcurrentHashMap<>();

    public ToolManager(ToolScanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 扫描并加载所有工具
     */
    public void loadTools() throws IOException {
        List<ToolConfig> loadedTools = scanner.scan();
        tools.clear();
        for (ToolConfig tool : loadedTools) {
            tools.put(tool.getId(), tool);
        }
    }

    /**
     * 获取工具列表
     */
    public List<ToolConfig> listTools() {
        return List.copyOf(tools.values());
    }

    /**
     * 根据 ID 获取工具
     */
    public Optional<ToolConfig> getTool(String toolId) {
        return Optional.ofNullable(tools.get(toolId));
    }

    /**
     * 检查工具是否存在
     */
    public boolean hasTool(String toolId) {
        return tools.containsKey(toolId);
    }

    /**
     * 获取工具数量
     */
    public int getToolCount() {
        return tools.size();
    }
}