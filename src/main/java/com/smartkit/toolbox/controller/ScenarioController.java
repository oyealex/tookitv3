package com.smartkit.toolbox.controller;

import com.smartkit.toolbox.annotation.LogOperation;
import com.smartkit.toolbox.model.OperationType;
import com.smartkit.toolbox.model.Result;
import com.smartkit.toolbox.model.dto.scenario.*;
import com.smartkit.toolbox.model.scenario.*;
import com.smartkit.toolbox.service.ToolService;
import com.smartkit.toolbox.service.scenario.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 场景管理控制器，提供场景相关的 REST API 接口。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/scenarios")
@Tag(name = "Scenario Management", description = "场景管理API")
@Validated
public class ScenarioController {

    /**
     * 场景服务
     */
    private final ScenarioService scenarioService;

    /**
     * 构造方法，注入依赖的服务
     *
     * @param scenarioService 场景服务
     */
    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    /**
     * 获取场景列表
     *
     * @return 场景列表
     */
    @GetMapping
    @Operation(summary = "获取场景列表")
    public Result<ScenarioListResponse> listScenarios() {
        List<ScenarioConfig> scenarios = scenarioService.listScenarios();
        List<ScenarioListResponse.ScenarioSummary> summaryList = scenarios.stream()
            .map(s -> ScenarioListResponse.ScenarioSummary.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .stepCount(s.getSteps().size())
                .build())
            .collect(Collectors.toList());
        return Result.success(new ScenarioListResponse(summaryList));
    }

    /**
     * 获取场景详情
     *
     * @param scenarioId 场景 ID
     * @return 场景详情
     */
    @GetMapping("/{scenarioId}")
    @Operation(summary = "获取场景详情")
    public Result<ScenarioDetailResponse> getScenario(@PathVariable @Pattern(regexp = "^[a-z0-9-]+$", message = "场景 ID 格式非法") String scenarioId) {
        ScenarioConfig config = scenarioService.getScenario(scenarioId);
        ScenarioDetailResponse response = ScenarioDetailResponse.builder()
            .id(config.getId())
            .name(config.getName())
            .description(config.getDescription())
            .steps(config.getSteps())
            .allowSkipOnFailure(config.getAllowSkipOnFailure())
            .allowSkipStep(config.getAllowSkipStep())
            .build();
        return Result.success(response);
    }

    /**
     * 启动场景
     *
     * @param scenarioId 场景 ID
     * @return 场景执行实例
     */
    @PostMapping("/{scenarioId}/start")
    @Operation(summary = "启动场景")
    @LogOperation(
        operationType = OperationType.UPDATE,
        objectId = "#scenarioId",
        objectName = "场景",
        description = "'启动场景: ' + #scenarioId",
        operatorIp = true
    )
    public Result<ScenarioStatusResponse> startScenario(@PathVariable @Pattern(regexp = "^[a-z0-9-]+$", message = "场景 ID 格式非法") String scenarioId) {
        ScenarioExecution execution = scenarioService.startScenario(scenarioId);
        ScenarioStatusResponse response = ScenarioStatusResponse.builder()
            .scenarioId(execution.getScenarioId())
            .scenarioName(execution.getScenarioName())
            .status(execution.getStatus())
            .build();
        return Result.success(response);
    }

    /**
     * 选择设备
     *
     * @param scenarioId 场景 ID
     * @param request 选择设备请求
     * @return 场景执行实例
     */
    @PostMapping("/{scenarioId}/devices")
    @Operation(summary = "选择设备")
    @LogOperation(
        operationType = OperationType.UPDATE,
        objectId = "#scenarioId",
        objectName = "场景",
        description = "'选择设备: ' + #scenarioId",
        operatorIp = true
    )
    public Result<ScenarioStatusResponse> selectDevices(
            @PathVariable @Pattern(regexp = "^[a-z0-9-]+$", message = "场景 ID 格式非法") String scenarioId,
            @Valid @RequestBody SelectDevicesRequest request) {
        ScenarioExecution execution = scenarioService.selectDevices(scenarioId, request.getDeviceIps());
        ScenarioStatusResponse response = ScenarioStatusResponse.builder()
            .scenarioId(execution.getScenarioId())
            .scenarioName(execution.getScenarioName())
            .status(execution.getStatus())
            .selectedDevices(execution.getSelectedDevices())
            .build();
        return Result.success(response);
    }

    /**
     * 修改设备范围
     *
     * @param scenarioId 场景 ID
     * @param request 修改设备请求
     * @return 场景执行实例
     */
    @PutMapping("/{scenarioId}/devices")
    @Operation(summary = "修改设备范围")
    @LogOperation(
        operationType = OperationType.UPDATE,
        objectId = "#scenarioId",
        objectName = "场景",
        description = "'修改设备范围: ' + #scenarioId",
        operatorIp = true
    )
    public Result<ScenarioStatusResponse> updateDevices(
            @PathVariable @Pattern(regexp = "^[a-z0-9-]+$", message = "场景 ID 格式非法") String scenarioId,
            @Valid @RequestBody UpdateDevicesRequest request) {
        ScenarioExecution execution = scenarioService.updateDevices(scenarioId, request.getDeviceIps());
        ScenarioStatusResponse response = ScenarioStatusResponse.builder()
            .scenarioId(execution.getScenarioId())
            .scenarioName(execution.getScenarioName())
            .status(execution.getStatus())
            .selectedDevices(execution.getSelectedDevices())
            .build();
        return Result.success(response);
    }

    /**
     * 确认开始执行
     *
     * @param scenarioId 场景 ID
     * @return 场景执行实例
     */
    @PostMapping("/{scenarioId}/confirm")
    @Operation(summary = "确认开始执行")
    @LogOperation(
        operationType = OperationType.UPDATE,
        objectId = "#scenarioId",
        objectName = "场景",
        description = "'确认开始执行: ' + #scenarioId",
        operatorIp = true
    )
    public Result<ScenarioStatusResponse> confirmDevices(@PathVariable @Pattern(regexp = "^[a-z0-9-]+$", message = "场景 ID 格式非法") String scenarioId) {
        ScenarioExecution execution = scenarioService.confirmDevices(scenarioId);
        ScenarioStatusResponse response = ScenarioStatusResponse.builder()
            .scenarioId(execution.getScenarioId())
            .scenarioName(execution.getScenarioName())
            .status(execution.getStatus())
            .currentStepIndex(execution.getCurrentStepIndex())
            .build();
        return Result.success(response);
    }

    /**
     * 查询执行状态
     *
     * @param scenarioId 场景 ID
     * @return 场景执行实例
     */
    @GetMapping("/{scenarioId}/status")
    @Operation(summary = "查询执行状态")
    public Result<ScenarioStatusResponse> getExecutionStatus(@PathVariable @Pattern(regexp = "^[a-z0-9-]+$", message = "场景 ID 格式非法") String scenarioId) {
        ScenarioExecution execution = scenarioService.getExecutionStatus(scenarioId);
        ScenarioStatusResponse response = ScenarioStatusResponse.builder()
            .scenarioId(execution.getScenarioId())
            .scenarioName(execution.getScenarioName())
            .status(execution.getStatus())
            .selectedDevices(execution.getSelectedDevices())
            .currentStepIndex(execution.getCurrentStepIndex())
            .build();
        return Result.success(response);
    }

    /**
     * 执行步骤
     *
     * @param scenarioId 场景 ID
     * @param stepIndex 步骤索引
     * @return 执行结果
     */
    @PostMapping("/{scenarioId}/steps/{stepIndex}/execute")
    @Operation(summary = "执行步骤")
    @LogOperation(
        operationType = OperationType.UPDATE,
        objectId = "#scenarioId",
        objectName = "场景",
        description = "'执行步骤: ' + #scenarioId + ' step ' + #stepIndex",
        operatorIp = true
    )
    public Result<ExecuteStepResponse> executeStep(
            @PathVariable @Pattern(regexp = "^[a-z0-9-]+$", message = "场景 ID 格式非法") String scenarioId,
            @PathVariable Integer stepIndex) {
        ToolService.ToolExecuteResult result = scenarioService.executeStep(scenarioId, stepIndex);
        return Result.success(new ExecuteStepResponse(result.success(), result.message()));
    }

    /**
     * 跳过步骤
     *
     * @param scenarioId 场景 ID
     * @param stepIndex 步骤索引
     * @return 场景执行实例
     */
    @PostMapping("/{scenarioId}/steps/{stepIndex}/skip")
    @Operation(summary = "跳过步骤")
    @LogOperation(
        operationType = OperationType.UPDATE,
        objectId = "#scenarioId",
        objectName = "场景",
        description = "'跳过步骤: ' + #scenarioId + ' step ' + #stepIndex",
        operatorIp = true
    )
    public Result<ScenarioStatusResponse> skipStep(
            @PathVariable @Pattern(regexp = "^[a-z0-9-]+$", message = "场景 ID 格式非法") String scenarioId,
            @PathVariable Integer stepIndex) {
        ScenarioExecution execution = scenarioService.skipStep(scenarioId);
        ScenarioStatusResponse response = ScenarioStatusResponse.builder()
            .scenarioId(execution.getScenarioId())
            .scenarioName(execution.getScenarioName())
            .status(execution.getStatus())
            .currentStepIndex(execution.getCurrentStepIndex())
            .build();
        return Result.success(response);
    }

    /**
     * 终止场景
     *
     * @param scenarioId 场景 ID
     * @return 终止结果
     */
    @PostMapping("/{scenarioId}/terminate")
    @Operation(summary = "终止场景")
    @LogOperation(
        operationType = OperationType.UPDATE,
        objectId = "#scenarioId",
        objectName = "场景",
        description = "'终止场景: ' + #scenarioId",
        operatorIp = true
    )
    public Result<TerminateScenarioResponse> terminateScenario(@PathVariable @Pattern(regexp = "^[a-z0-9-]+$", message = "场景 ID 格式非法") String scenarioId) {
        ScenarioExecution execution = scenarioService.terminateScenario(scenarioId);
        TerminateScenarioResponse response = TerminateScenarioResponse.builder()
            .scenarioId(execution.getScenarioId())
            .status(execution.getStatus().name())
            .build();
        return Result.success(response);
    }
}
