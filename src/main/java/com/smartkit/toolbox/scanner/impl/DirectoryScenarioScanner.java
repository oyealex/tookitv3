package com.smartkit.toolbox.scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartkit.toolbox.common.BusinessException;
import com.smartkit.toolbox.model.scenario.ScenarioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 目录场景扫描器
 * 扫描 scenarios 目录下的场景配置文件
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Component
public class DirectoryScenarioScanner implements ScenarioScanner {

    private static final Logger log = LoggerFactory.getLogger(DirectoryScenarioScanner.class);

    /**
     * 场景配置文件名
     */
    private static final String SCENARIO_FILE_NAME = "scenario.json";

    /**
     * 场景 ID 合法性校验正则表达式（只允许小写字母、数字、连字符）
     */
    private static final Pattern VALID_ID_PATTERN = Pattern.compile("^[a-z0-9-]{1,64}$");

    private final ObjectMapper objectMapper;

    public DirectoryScenarioScanner() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<ScenarioConfig> scan(Path scenariosDir) {
        List<ScenarioConfig> scenarios = new ArrayList<>();

        if (scenariosDir == null || !Files.exists(scenariosDir)) {
            log.info("场景目录不存在: {}", scenariosDir);
            return scenarios;
        }

        log.info("开始扫描场景目录: {}", scenariosDir);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(scenariosDir)) {
            for (Path entry : directoryStream) {
                if (Files.isDirectory(entry)) {
                    ScenarioConfig config = loadScenarioConfig(entry);
                    if (config != null) {
                        scenarios.add(config);
                    }
                }
            }
        } catch (IOException e) {
            log.error("扫描场景目录失败: {}", scenariosDir, e);
        }

        log.info("扫描完成，发现 {} 个场景", scenarios.size());
        return scenarios;
    }

    /**
     * 加载单个场景配置文件
     *
     * @param scenarioDir 场景目录
     * @return 场景配置，失败返回 null
     */
    private ScenarioConfig loadScenarioConfig(Path scenarioDir) {
        Path configFile = scenarioDir.resolve(SCENARIO_FILE_NAME);

        if (!Files.exists(configFile)) {
            log.warn("场景配置文件不存在: {}", configFile);
            return null;
        }

        try {
            ScenarioConfig config = objectMapper.readValue(configFile.toFile(), ScenarioConfig.class);

            // 验证必需字段
            if (!validateScenarioConfig(config, scenarioDir.getFileName().toString())) {
                return null;
            }

            log.info("加载场景配置成功: id={}, name={}", config.getId(), config.getName());
            return config;

        } catch (IOException e) {
            log.error("解析场景配置文件失败: {}", configFile, e);
            return null;
        }
    }

    /**
     * 验证场景配置的有效性
     *
     * @param config 场景配置
     * @param directoryName 场景目录名
     * @return 是否有效
     */
    private boolean validateScenarioConfig(ScenarioConfig config, String directoryName) {
        // 验证 ID
        String id = config.getId();
        if (id == null || id.isEmpty()) {
            log.error("场景配置缺少必需字段 id: directory={}", directoryName);
            return false;
        }

        // 验证 ID 格式
        if (!VALID_ID_PATTERN.matcher(id).matches()) {
            log.error("场景 ID 格式非法: id={}, directory={}", id, directoryName);
            return false;
        }

        // 验证步骤列表
        if (config.getSteps() == null || config.getSteps().isEmpty()) {
            log.error("场景配置缺少必需字段 steps: id={}", id);
            return false;
        }

        // 验证步骤中的 toolId
        for (int i = 0; i < config.getSteps().size(); i++) {
            String toolId = config.getSteps().get(i).getToolId();
            if (toolId == null || toolId.isEmpty()) {
                log.error("场景步骤缺少必需字段 toolId: id={}, stepIndex={}", id, i);
                return false;
            }
        }

        return true;
    }
}
