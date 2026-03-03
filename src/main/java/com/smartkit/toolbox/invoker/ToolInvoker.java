package com.smartkit.toolbox.invoker;

import com.smartkit.toolbox.model.tool.ToolConfig;
import com.smartkit.toolbox.model.tool.ToolResult;

import java.util.List;

/**
 * 子工具调用器接口
 */
public interface ToolInvoker {

    /**
     * 执行子工具
     *
     * @param tool       工具配置
     * @param workDir    工作目录
     * @param inputFile  输入文件路径
     * @return 执行结果的 Future
     */
    ToolInvokeResult invoke(ToolConfig tool, String workDir, String inputFile);

    /**
     * 获取工具所在目录
     *
     * @param toolId 工具 ID
     * @return 工具目录路径
     */
    String getToolDirectory(String toolId);

    /**
     * 终止正在运行的工具
     *
     * @param processId 进程 ID
     * @return 是否成功终止
     */
    boolean terminate(String processId);
}