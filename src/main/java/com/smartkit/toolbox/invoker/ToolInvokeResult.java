package com.smartkit.toolbox.invoker;

import lombok.Builder;
import lombok.Data;

/**
 * 工具调用结果
 */
@Data
@Builder
public class ToolInvokeResult {

    /**
     * 进程 ID
     */
    private String processId;

    /**
     * 工作目录
     */
    private String workDir;

    /**
     * 工具是否成功启动
     */
    private boolean started;

    /**
     * 错误信息（如果启动失败）
     */
    private String errorMessage;
}