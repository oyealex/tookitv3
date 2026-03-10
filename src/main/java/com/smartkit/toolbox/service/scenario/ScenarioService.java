package com.smartkit.toolbox.service.scenario;

import com.smartkit.toolbox.common.BusinessException;
import com.smartkit.toolbox.lock.ToolLockManager;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.ScenarioErrorCode;
import com.smartkit.toolbox.model.scenario.*;
import com.smartkit.toolbox.model.tool.*;
import com.smartkit.toolbox.scanner.ScenarioManager;
import com.smartkit.toolbox.scanner.ScenarioScanner;
import com.smartkit.toolbox.service.DeviceLockService;
import com.smartkit.toolbox.service.DeviceService;
import com.smartkit.toolbox.service.ToolService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 场景服务类
 * 处理场景执行逻辑
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Service
public class ScenarioService {

    private static final Logger log = LoggerFactory.getLogger(ScenarioService.class);

    /**
     * 场景配置目录
     */
    private static final Path SCENARIOS_DIR = Paths.get("data", "scenarios");

    /**
     * 场景执行实例存储
     */
    private final Map<String, ScenarioExecution> executions = new HashMap<>();

    /**
     * 全局执行锁（同一时间只能执行一个场景）
     */
    private final Object executionLock = new Object();

    /**
     * 场景管理器
     */
    private final ScenarioManager scenarioManager;

    /**
     * 场景扫描器
     */
    private final ScenarioScanner scenarioScanner;

    /**
     * 设备服务
     */
    private final DeviceService deviceService;

    /**
     * 设备锁服务
     */
    private final DeviceLockService deviceLockService;

    /**
     * 工具服务
     */
    private final ToolService toolService;

    /**
     * 工具锁管理器
     */
    private final ToolLockManager toolLockManager;

    /**
     * 消息源，用于国际化
     */
    private final MessageSource messageSource;

    /**
     * 构造方法，注入依赖
     *
     * @param scenarioManager 场景管理器
     * @param scenarioScanner 场景扫描器
     * @param deviceService 设备服务
     * @param deviceLockService 设备锁服务
     * @param toolService 工具服务
     * @param toolLockManager 工具锁管理器
     * @param messageSource 消息源
     */
    public ScenarioService(ScenarioManager scenarioManager,
                          ScenarioScanner scenarioScanner,
                          DeviceService deviceService,
                          DeviceLockService deviceLockService,
                          ToolService toolService,
                          ToolLockManager toolLockManager,
                          MessageSource messageSource) {
        this.scenarioManager = scenarioManager;
        this.scenarioScanner = scenarioScanner;
        this.deviceService = deviceService;
        this.deviceLockService = deviceLockService;
        this.toolService = toolService;
        this.toolLockManager = toolLockManager;
        this.messageSource = messageSource;
    }

    /**
     * 获取国际化消息的辅助方法
     *
     * @param key 消息key
     * @param args 消息参数
     * @return 国际化消息
     */
    private String msg(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * 初始化，加载场景配置
     */
    public void init() {
        try {
            if (Files.exists(SCENARIOS_DIR)) {
                scenarioManager.loadScenarios(SCENARIOS_DIR);
            } else {
                log.info("场景目录不存在，创建目录: {}", SCENARIOS_DIR);
                Files.createDirectories(SCENARIOS_DIR);
            }
        } catch (Exception e) {
            log.error("加载场景配置失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 加载场景配置
     *
     * @return 场景列表
     */
    public List<ScenarioConfig> loadScenarios() {
        return scenarioManager.getAllScenarios();
    }

    /**
     * 获取场景列表
     *
     * @return 场景列表
     */
    public List<ScenarioConfig> listScenarios() {
        return scenarioManager.getAllScenarios();
    }

    /**
     * 获取场景详情
     *
     * @param scenarioId 场景 ID
     * @return 场景详情
     */
    public ScenarioConfig getScenario(String scenarioId) {
        ScenarioConfig config = scenarioManager.getScenario(scenarioId);
        if (config == null) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_NOT_FOUND.getMessageKey()));
        }
        return config;
    }

    /**
     * 启动场景，状态转换 IDLE -> DEVICE_SELECTION
     *
     * @param scenarioId 场景 ID
     * @return 场景执行实例
     */
    public ScenarioExecution startScenario(String scenarioId) {
        ScenarioConfig config = getScenario(scenarioId);

        // 尝试获取场景锁，确保没有工具或场景正在执行
        ToolLockManager.LockResult lockResult = toolLockManager.tryLockScenario(scenarioId, config.getName(), 0);
        if (!lockResult.success()) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_LOCKED.getCode(),
                msg(ScenarioErrorCode.SCENARIO_LOCKED.getMessageKey()) + ": " + lockResult.message());
        }

        // 检查是否有场景正在执行
        synchronized (executionLock) {
            if (executions.values().stream().anyMatch(e -> e.getStatus() != ScenarioStatus.COMPLETED && e.getStatus() != ScenarioStatus.FAILED)) {
                // 释放锁
                toolLockManager.unlockScenario(scenarioId);
                throw new BusinessException(ScenarioErrorCode.SCENARIO_LOCKED.getCode(),
                    msg(ScenarioErrorCode.SCENARIO_LOCKED.getMessageKey()));
            }

            ScenarioExecution execution = new ScenarioExecution();
            execution.setScenarioId(scenarioId);
            execution.setScenarioName(config.getName());
            execution.setStatus(ScenarioStatus.DEVICE_SELECTION);
            execution.setCurrentStepIndex(-1); // -1 表示尚未开始选择设备
            execution.setLockSource("scenario-" + scenarioId);
            execution.setStepExecutions(new ArrayList<>());

            executions.put(scenarioId, execution);

            log.info("启动场景: id={}, name={}", scenarioId, config.getName());
            return execution;
        }
    }

    /**
     * 选择设备，锁定所选设备
     *
     * @param scenarioId 场景 ID
     * @param deviceIps 设备 IP 列表
     * @return 场景执行实例
     */
    public ScenarioExecution selectDevices(String scenarioId, List<String> deviceIps) {
        ScenarioExecution execution = getExecution(scenarioId);

        if (execution.getStatus() != ScenarioStatus.DEVICE_SELECTION) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }

        // 验证设备存在
        List<String> notFound = new ArrayList<>();
        for (String ip : deviceIps) {
            try {
                deviceService.getDevice(ip);
            } catch (BusinessException e) {
                notFound.add(ip);
            }
        }
        if (!notFound.isEmpty()) {
            throw new BusinessException(ScenarioErrorCode.DEVICE_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.DEVICE_NOT_FOUND.getMessageKey()));
        }

        // 锁定设备
        try {
            deviceLockService.lockDevices(deviceIps, execution.getLockSource());
        } catch (DeviceLockService.LockException e) {
            throw new BusinessException(ScenarioErrorCode.DEVICE_LOCKED.getCode(),
                msg(ScenarioErrorCode.DEVICE_LOCKED.getMessageKey()));
        }

        execution.setSelectedDevices(deviceIps);
        log.info("选择设备: scenarioId={}, devices={}", scenarioId, deviceIps);
        return execution;
    }

    /**
     * 修改设备范围（仅在第一个工具启动前允许）
     *
     * @param scenarioId 场景 ID
     * @param deviceIps 新的设备 IP 列表
     * @return 场景执行实例
     */
    public ScenarioExecution updateDevices(String scenarioId, List<String> deviceIps) {
        ScenarioExecution execution = getExecution(scenarioId);

        if (execution.getStatus() != ScenarioStatus.DEVICE_SELECTION) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }

        // 检查是否有工具已执行
        boolean hasExecution = execution.getStepExecutions().stream()
            .anyMatch(e -> e.getStatus() == StepStatus.RUNNING || e.getStatus() == StepStatus.COMPLETED || e.getStatus() == StepStatus.FAILED);

        if (hasExecution) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }

        // 解锁旧设备
        if (execution.getSelectedDevices() != null) {
            deviceLockService.unlockBySource(execution.getLockSource());
        }

        // 锁定新设备
        try {
            deviceLockService.lockDevices(deviceIps, execution.getLockSource());
        } catch (DeviceLockService.LockException e) {
            throw new BusinessException(ScenarioErrorCode.DEVICE_LOCKED.getCode(),
                msg(ScenarioErrorCode.DEVICE_LOCKED.getMessageKey()));
        }

        execution.setSelectedDevices(deviceIps);
        log.info("修改设备: scenarioId={}, devices={}", scenarioId, deviceIps);
        return execution;
    }

    /**
     * 确认开始执行，状态转换 DEVICE_SELECTION -> RUNNING
     *
     * @param scenarioId 场景 ID
     * @return 场景执行实例
     */
    public ScenarioExecution confirmDevices(String scenarioId) {
        ScenarioExecution execution = getExecution(scenarioId);

        if (execution.getStatus() != ScenarioStatus.DEVICE_SELECTION) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }

        if (execution.getSelectedDevices() == null || execution.getSelectedDevices().isEmpty()) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }

        // 设置第一个步骤为 pending
        ScenarioConfig config = scenarioManager.getScenario(scenarioId);
        if (config.getSteps() != null && !config.getSteps().isEmpty()) {
            execution.setCurrentStepIndex(0);
            ScenarioStep step = config.getSteps().get(0);
            ScenarioStepExecution stepExecution = new ScenarioStepExecution();
            stepExecution.setStepIndex(0);
            stepExecution.setToolId(step.getToolId());
            stepExecution.setName(step.getName());
            stepExecution.setStatus(StepStatus.PENDING);
            execution.getStepExecutions().add(stepExecution);
        }

        execution.setStatus(ScenarioStatus.RUNNING);
        log.info("确认开始执行: scenarioId={}", scenarioId);
        return execution;
    }

    /**
     * 获取当前步骤信息
     *
     * @param scenarioId 场景 ID
     * @return 当前步骤信息
     */
    public ScenarioStep getCurrentStep(String scenarioId) {
        ScenarioExecution execution = getExecution(scenarioId);

        if (execution.getStatus() != ScenarioStatus.RUNNING) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }

        int currentIndex = execution.getCurrentStepIndex();
        if (currentIndex < 0) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }

        ScenarioConfig config = scenarioManager.getScenario(scenarioId);
        if (config.getSteps() == null || currentIndex >= config.getSteps().size()) {
            return null;
        }

        return config.getSteps().get(currentIndex);
    }

    /**
     * 执行步骤，启动工具
     *
     * @param scenarioId 场景 ID
     * @param stepIndex 步骤索引
     * @return 工具执行结果
     */
    public ToolService.ToolExecuteResult executeStep(String scenarioId, int stepIndex) {
        ScenarioExecution execution = getExecution(scenarioId);

        if (execution.getStatus() != ScenarioStatus.RUNNING) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }

        if (stepIndex != execution.getCurrentStepIndex()) {
            throw new BusinessException(ScenarioErrorCode.INVALID_STEP_INDEX.getCode(),
                msg(ScenarioErrorCode.INVALID_STEP_INDEX.getMessageKey()));
        }

        // 检查步骤状态
        if (stepIndex >= execution.getStepExecutions().size()) {
            throw new BusinessException(ScenarioErrorCode.INVALID_STEP_INDEX.getCode(),
                msg(ScenarioErrorCode.INVALID_STEP_INDEX.getMessageKey()));
        }

        ScenarioStepExecution stepExecution = execution.getStepExecutions().get(stepIndex);
        if (stepExecution.getStatus() != StepStatus.PENDING) {
            throw new BusinessException(ScenarioErrorCode.STEP_ALREADY_EXECUTED.getCode(),
                msg(ScenarioErrorCode.STEP_ALREADY_EXECUTED.getMessageKey()));
        }

        // 设置步骤状态为 running
        stepExecution.setStatus(StepStatus.RUNNING);
        stepExecution.setStartTime(System.currentTimeMillis());

        // 获取场景配置
        ScenarioConfig config = scenarioManager.getScenario(scenarioId);
        ScenarioStep step = config.getSteps().get(stepIndex);

        // 构建工具输入
        ToolInput toolInput = buildToolInput(scenarioId, stepIndex, step.getToolId(), config);

        log.info("执行步骤: scenarioId={}, stepIndex={}, toolId={}", scenarioId, stepIndex, step.getToolId());

        // 调用工具服务
        // 注意：这里简化处理，实际应通过 ToolService 的 executeToolInScenario 方法
        ToolService.ToolExecuteResult result = toolService.executeTool(step.getToolId(), execution.getSelectedDevices());

        // 处理执行结果
        long endTime = System.currentTimeMillis();
        stepExecution.setEndTime(endTime);
        stepExecution.setResult(result.success() ? "success" : "failure");

        if (result.success()) {
            stepExecution.setStatus(StepStatus.COMPLETED);
            log.info("步骤执行完成: scenarioId={}, stepIndex={}", scenarioId, stepIndex);
        } else {
            // 检查是否允许跳过
            if (config.getAllowSkipOnFailure() != null && config.getAllowSkipOnFailure()) {
                stepExecution.setStatus(StepStatus.SKIPPED);
                log.warn("步骤执行失败但允许跳过: scenarioId={}, stepIndex={}", scenarioId, stepIndex);
            } else {
                stepExecution.setStatus(StepStatus.FAILED);
                execution.setStatus(ScenarioStatus.FAILED);
                unlockAllDevices(execution);

                // 释放场景锁
                toolLockManager.unlockScenario(scenarioId);

                log.error("步骤执行失败: scenarioId={}, stepIndex={}", scenarioId, stepIndex);
            }
        }

        return result;
    }

    /**
     * 构建工具输入
     *
     * @param scenarioId 场景 ID
     * @param stepIndex 步骤索引
     * @param toolId 工具 ID
     * @param config 场景配置
     * @return 工具输入
     */
    private ToolInput buildToolInput(String scenarioId, int stepIndex, String toolId, ScenarioConfig config) {
        ScenarioExecution execution = executions.get(scenarioId);

        List<DeviceInfo> deviceInfos = execution.getSelectedDevices().stream()
            .map(ip -> {
                Device device = deviceService.getDevice(ip);
                return DeviceInfo.builder()
                    .ip(device.getIp())
                    .name(device.getName())
                    .port(22)
                    .username(device.getUsername())
                    .password(device.getPassword())
                    .build();
            })
            .toList();

        // 构建场景信息
        ScenarioInfo scenarioInfo = ScenarioInfo.builder()
            .scenarioId(scenarioId)
            .scenarioName(config.getName())
            .stepIndex(stepIndex)
            .build();

        ToolInput toolInput = ToolInput.builder()
            .toolId(toolId)
            .toolName(config.getSteps().get(stepIndex).getName())
            .devices(deviceInfos)
            .scenario(scenarioInfo)
            .build();

        // 如果不是第一步，添加前置结果
        if (stepIndex > 0) {
            ScenarioStepExecution prevExecution = execution.getStepExecutions().get(stepIndex - 1);
            if (prevExecution != null && prevExecution.getStartTime() != null) {
                PreviousResult previousResult = new PreviousResult();
                previousResult.setStatus(prevExecution.getStatus().name().toLowerCase());
                previousResult.setResult(prevExecution.getResult());
                // 这里简化处理，实际应从工具执行结果中获取
                toolInput.setPreviousResult(previousResult);
            }
        }

        return toolInput;
    }

    /**
     * 跳过当前未执行的步骤
     *
     * @param scenarioId 场景 ID
     * @return 场景执行实例
     */
    public ScenarioExecution skipStep(String scenarioId) {
        ScenarioExecution execution = getExecution(scenarioId);

        if (execution.getStatus() != ScenarioStatus.RUNNING) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }

        int currentIndex = execution.getCurrentStepIndex();
        if (currentIndex < 0 || currentIndex >= execution.getStepExecutions().size()) {
            throw new BusinessException(ScenarioErrorCode.INVALID_STEP_INDEX.getCode(),
                msg(ScenarioErrorCode.INVALID_STEP_INDEX.getMessageKey()));
        }

        ScenarioStepExecution stepExecution = execution.getStepExecutions().get(currentIndex);
        if (stepExecution.getStatus() != StepStatus.PENDING) {
            throw new BusinessException(ScenarioErrorCode.STEP_ALREADY_EXECUTED.getCode(),
                msg(ScenarioErrorCode.STEP_ALREADY_EXECUTED.getMessageKey()));
        }

        // 检查是否允许跳过
        ScenarioConfig config = scenarioManager.getScenario(scenarioId);
        if (config.getAllowSkipStep() == null || !config.getAllowSkipStep()) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_NOT_ALLOW_SKIP.getCode(),
                msg(ScenarioErrorCode.SCENARIO_NOT_ALLOW_SKIP.getMessageKey()));
        }

        stepExecution.setStatus(StepStatus.SKIPPED);
        stepExecution.setEndTime(System.currentTimeMillis());

        // 推进到下一步
        advanceStep(execution, config);

        log.info("跳过步骤: scenarioId={}, stepIndex={}", scenarioId, currentIndex);
        return execution;
    }

    /**
     * 推进到下一步骤
     *
     * @param execution 场景执行实例
     * @param config 场景配置
     */
    private void advanceStep(ScenarioExecution execution, ScenarioConfig config) {
        int currentIndex = execution.getCurrentStepIndex();
        int nextIndex = currentIndex + 1;

        if (nextIndex >= config.getSteps().size()) {
            // 所有步骤完成
            execution.setStatus(ScenarioStatus.COMPLETED);
            unlockAllDevices(execution);

            // 释放场景锁
            toolLockManager.unlockScenario(execution.getScenarioId());

            log.info("场景执行完成: scenarioId={}", execution.getScenarioId());
        } else {
            // 推进到下一步
            execution.setCurrentStepIndex(nextIndex);
            ScenarioStep step = config.getSteps().get(nextIndex);
            ScenarioStepExecution stepExecution = new ScenarioStepExecution();
            stepExecution.setStepIndex(nextIndex);
            stepExecution.setToolId(step.getToolId());
            stepExecution.setName(step.getName());
            stepExecution.setStatus(StepStatus.PENDING);
            execution.getStepExecutions().add(stepExecution);
        }
    }

    /**
     * 终止场景，解锁所有设备
     *
     * @param scenarioId 场景 ID
     * @return 场景执行实例
     */
    public ScenarioExecution terminateScenario(String scenarioId) {
        ScenarioExecution execution = getExecution(scenarioId);

        if (execution.getStatus() == ScenarioStatus.COMPLETED || execution.getStatus() == ScenarioStatus.FAILED) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_ALREADY_END.getCode(),
                msg(ScenarioErrorCode.SCENARIO_ALREADY_END.getMessageKey()));
        }

        execution.setStatus(ScenarioStatus.FAILED);
        unlockAllDevices(execution);

        // 释放场景锁
        toolLockManager.unlockScenario(scenarioId);

        log.info("终止场景: scenarioId={}", scenarioId);
        return execution;
    }

    /**
     * 获取场景执行状态
     *
     * @param scenarioId 场景 ID
     * @return 场景执行实例
     */
    public ScenarioExecution getExecutionStatus(String scenarioId) {
        return getExecution(scenarioId);
    }

    /**
     * 获取场景执行实例
     *
     * @param scenarioId 场景 ID
     * @return 场景执行实例
     */
    private ScenarioExecution getExecution(String scenarioId) {
        ScenarioExecution execution = executions.get(scenarioId);
        if (execution == null) {
            throw new BusinessException(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getCode(),
                msg(ScenarioErrorCode.SCENARIO_EXECUTION_NOT_FOUND.getMessageKey()));
        }
        return execution;
    }

    /**
     * 解锁所有设备
     *
     * @param execution 场景执行实例
     */
    private void unlockAllDevices(ScenarioExecution execution) {
        if (execution.getSelectedDevices() != null) {
            deviceLockService.unlockBySource(execution.getLockSource());
            log.info("解锁所有设备: scenarioId={}, devices={}", execution.getScenarioId(), execution.getSelectedDevices());
        }
    }
}
