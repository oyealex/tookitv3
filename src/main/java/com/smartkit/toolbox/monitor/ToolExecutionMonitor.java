package com.smartkit.toolbox.monitor;

import com.smartkit.toolbox.model.tool.ToolResult;

/**
 * 工具执行监控器接口
 */
public interface ToolExecutionMonitor {

    /**
     * 启动监控
     *
     * @param workDir 工作目录
     */
    void startMonitoring(String workDir);

    /**
     * 等待工具执行完成
     *
     * @return 执行结果
     * @throws InterruptedException 如果等待过程中被中断
     */
    ToolResult waitForCompletion() throws InterruptedException;

    /**
     * 检查是否正在监控
     */
    boolean isMonitoring();

    /**
     * 停止监控
     */
    void stopMonitoring();
}