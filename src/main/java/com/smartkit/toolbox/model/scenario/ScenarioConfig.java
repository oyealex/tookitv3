package com.smartkit.toolbox.model.scenario;

import java.util.List;

/**
 * 场景配置模型
 * 对应 scenario.json 配置文件
 *
 * @author SmartKit
 * @since 1.0.0
 */
public class ScenarioConfig {

    /**
     * 场景唯一标识，kebab-case 格式
     */
    private String id;

    /**
     * 场景显示名称
     */
    private String name;

    /**
     * 场景描述（可选）
     */
    private String description;

    /**
     * 步骤列表，按顺序定义要执行的工具
     */
    private List<ScenarioStep> steps;

    /**
     * 子工具执行失败后是否允许跳过继续执行（可选，默认 false）
     */
    private Boolean allowSkipOnFailure;

    /**
     * 是否允许跳过未执行的子工具（可选，默认 false）
     */
    private Boolean allowSkipStep;

    /**
     * 构造方法
     */
    public ScenarioConfig() {
    }

    /**
     * 获取场景 ID
     *
     * @return 场景 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置场景 ID
     *
     * @param id 场景 ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取场景名称
     *
     * @return 场景名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置场景名称
     *
     * @param name 场景名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取场景描述
     *
     * @return 场景描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置场景描述
     *
     * @param description 场景描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取步骤列表
     *
     * @return 步骤列表
     */
    public List<ScenarioStep> getSteps() {
        return steps;
    }

    /**
     * 设置步骤列表
     *
     * @param steps 步骤列表
     */
    public void setSteps(List<ScenarioStep> steps) {
        this.steps = steps;
    }

    /**
     * 获取执行失败时是否允许跳过
     *
     * @return 是否允许跳过
     */
    public Boolean getAllowSkipOnFailure() {
        return allowSkipOnFailure;
    }

    /**
     * 设置执行失败时是否允许跳过
     *
     * @param allowSkipOnFailure 是否允许跳过
     */
    public void setAllowSkipOnFailure(Boolean allowSkipOnFailure) {
        this.allowSkipOnFailure = allowSkipOnFailure;
    }

    /**
     * 获取是否允许跳过未执行的步骤
     *
     * @return 是否允许跳过
     */
    public Boolean getAllowSkipStep() {
        return allowSkipStep;
    }

    /**
     * 设置是否允许跳过未执行的步骤
     *
     * @param allowSkipStep 是否允许跳过
     */
    public void setAllowSkipStep(Boolean allowSkipStep) {
        this.allowSkipStep = allowSkipStep;
    }
}
