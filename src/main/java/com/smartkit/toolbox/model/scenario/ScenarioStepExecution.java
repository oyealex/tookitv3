package com.smartkit.toolbox.model.scenario;

/**
 * 场景步骤执行记录模型
 * 记录单个步骤的执行状态和结果
 *
 * @author SmartKit
 * @since 1.0.0
 */
public class ScenarioStepExecution {

    /**
     * 步骤索引
     */
    private int stepIndex;

    /**
     * 工具 ID
     */
    private String toolId;

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 执行状态
     */
    private StepStatus status;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 执行结果（可选）
     */
    private String result;

    /**
     * 构造方法
     */
    public ScenarioStepExecution() {
    }

    /**
     * 获取步骤索引
     *
     * @return 步骤索引
     */
    public int getStepIndex() {
        return stepIndex;
    }

    /**
     * 设置步骤索引
     *
     * @param stepIndex 步骤索引
     */
    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    /**
     * 获取工具 ID
     *
     * @return 工具 ID
     */
    public String getToolId() {
        return toolId;
    }

    /**
     * 设置工具 ID
     *
     * @param toolId 工具 ID
     */
    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    /**
     * 获取步骤名称
     *
     * @return 步骤名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置步骤名称
     *
     * @param name 步骤名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取执行状态
     *
     * @return 执行状态
     */
    public StepStatus getStatus() {
        return status;
    }

    /**
     * 设置执行状态
     *
     * @param status 执行状态
     */
    public void setStatus(StepStatus status) {
        this.status = status;
    }

    /**
     * 获取开始时间
     *
     * @return 开始时间
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * 设置开始时间
     *
     * @param startTime 开始时间
     */
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取结束时间
     *
     * @return 结束时间
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间
     *
     * @param endTime 结束时间
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
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
}
