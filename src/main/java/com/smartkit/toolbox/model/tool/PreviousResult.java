package com.smartkit.toolbox.model.tool;

import com.smartkit.toolbox.model.tool.DeviceResult;

import java.util.List;

/**
 * 前置工具执行结果
 * 传递给后续工具的执行结果信息
 *
 * @author SmartKit
 * @since 1.0.0
 */
public class PreviousResult {

    /**
     * 执行状态
     */
    private String status;

    /**
     * 执行结果
     */
    private String result;

    /**
     * 设备结果列表
     */
    private List<DeviceResult> deviceResults;

    /**
     * 交付件列表
     */
    private List<Artifact> artifacts;

    /**
     * 构造方法
     */
    public PreviousResult() {
    }

    /**
     * 获取执行状态
     *
     * @return 执行状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置执行状态
     *
     * @param status 执行状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取执行结果
     *
     * @return 执行结果
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置执行结果
     *
     * @param result 执行结果
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * 获取设备结果列表
     *
     * @return 设备结果列表
     */
    public List<DeviceResult> getDeviceResults() {
        return deviceResults;
    }

    /**
     * 设置设备结果列表
     *
     * @param deviceResults 设备结果列表
     */
    public void setDeviceResults(List<DeviceResult> deviceResults) {
        this.deviceResults = deviceResults;
    }

    /**
     * 获取交付件列表
     *
     * @return 交付件列表
     */
    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    /**
     * 设置交付件列表
     *
     * @param artifacts 交付件列表
     */
    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }
}
