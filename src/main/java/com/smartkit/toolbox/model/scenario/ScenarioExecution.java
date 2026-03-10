package com.smartkit.toolbox.model.scenario;

import java.util.List;

/**
 * 场景执行模型
 * 维护场景执行实例的状态
 *
 * @author SmartKit
 * @since 1.0.0
 */
public class ScenarioExecution {

    /**
     * 场景 ID
     */
    private String scenarioId;

    /**
     * 场景名称
     */
    private String scenarioName;

    /**
     * 当前状态
     */
    private ScenarioStatus status;

    /**
     * 已选中的设备列表
     */
    private List<String> selectedDevices;

    /**
     * 当前步骤索引
     */
    private int currentStepIndex;

    /**
     * 步骤执行记录列表
     */
    private List<ScenarioStepExecution> stepExecutions;

    /**
     * 锁定来源（用于按来源解锁）
     */
    private String lockSource;

    /**
     * 构造方法
     */
    public ScenarioExecution() {
    }

    /**
     * 获取场景 ID
     *
     * @return 场景 ID
     */
    public String getScenarioId() {
        return scenarioId;
    }

    /**
     * 设置场景 ID
     *
     * @param scenarioId 场景 ID
     */
    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    /**
     * 获取场景名称
     *
     * @return 场景名称
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * 设置场景名称
     *
     * @param scenarioName 场景名称
     */
    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    public ScenarioStatus getStatus() {
        return status;
    }

    /**
     * 设置当前状态
     *
     * @param status 当前状态
     */
    public void setStatus(ScenarioStatus status) {
        this.status = status;
    }

    /**
     * 获取已选中的设备列表
     *
     * @return 已选中的设备列表
     */
    public List<String> getSelectedDevices() {
        return selectedDevices;
    }

    /**
     * 设置已选中的设备列表
     *
     * @param selectedDevices 已选中的设备列表
     */
    public void setSelectedDevices(List<String> selectedDevices) {
        this.selectedDevices = selectedDevices;
    }

    /**
     * 获取当前步骤索引
     *
     * @return 当前步骤索引
     */
    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    /**
     * 设置当前步骤索引
     *
     * @param currentStepIndex 当前步骤索引
     */
    public void setCurrentStepIndex(int currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }

    /**
     * 获取步骤执行记录列表
     *
     * @return 步骤执行记录列表
     */
    public List<ScenarioStepExecution> getStepExecutions() {
        return stepExecutions;
    }

    /**
     * 设置步骤执行记录列表
     *
     * @param stepExecutions 步骤执行记录列表
     */
    public void setStepExecutions(List<ScenarioStepExecution> stepExecutions) {
        this.stepExecutions = stepExecutions;
    }

    /**
     * 获取锁定来源
     *
     * @return 锁定来源
     */
    public String getLockSource() {
        return lockSource;
    }

    /**
     * 设置锁定来源
     *
     * @param lockSource 锁定来源
     */
    public void setLockSource(String lockSource) {
        this.lockSource = lockSource;
    }
}
