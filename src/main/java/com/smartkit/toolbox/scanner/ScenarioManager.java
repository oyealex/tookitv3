package com.smartkit.toolbox.scanner;

import com.smartkit.toolbox.model.scenario.ScenarioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 场景管理器
 * 管理已加载的场景配置
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Component
public class ScenarioManager {

    private static final Logger log = LoggerFactory.getLogger(ScenarioManager.class);

    /**
     * 场景配置存储（ID -> ScenarioConfig）
     */
    private final Map<String, ScenarioConfig> scenarios = new ConcurrentHashMap<>();

    /**
     * 场景扫描器
     */
    private final ScenarioScanner scanner;

    /**
     * 构造方法，注入依赖
     *
     * @param scanner 场景扫描器
     */
    @Autowired
    public ScenarioManager(ScenarioScanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 扫描并加载场景配置
     *
     * @param scenariosDir 场景配置目录
     * @return 加载的场景数量
     */
    public int loadScenarios(Path scenariosDir) {
        scenarios.clear();
        List<ScenarioConfig> loadedScenarios = scanner.scan(scenariosDir);

        for (ScenarioConfig config : loadedScenarios) {
            scenarios.put(config.getId(), config);
        }

        log.info("加载场景配置完成，共 {} 个", scenarios.size());
        return scenarios.size();
    }

    /**
     * 获取所有场景配置
     *
     * @return 场景配置列表
     */
    public List<ScenarioConfig> getAllScenarios() {
        return new ArrayList<>(scenarios.values());
    }

    /**
     * 根据 ID 获取场景配置
     *
     * @param scenarioId 场景 ID
     * @return 场景配置，不存在返回 null
     */
    public ScenarioConfig getScenario(String scenarioId) {
        return scenarios.get(scenarioId);
    }

    /**
     * 获取场景数量
     *
     * @return 场景数量
     */
    public int getScenarioCount() {
        return scenarios.size();
    }

    /**
     * 检查场景是否存在
     *
     * @param scenarioId 场景 ID
     * @return 是否存在
     */
    public boolean exists(String scenarioId) {
        return scenarios.containsKey(scenarioId);
    }
}
