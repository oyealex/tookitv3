package com.smartkit.toolbox.scanner;

import com.smartkit.toolbox.model.scenario.ScenarioConfig;

import java.nio.file.Path;
import java.util.List;

/**
 * 场景扫描器接口
 * 定义场景配置文件的扫描规范
 *
 * @author SmartKit
 * @since 1.0.0
 */
public interface ScenarioScanner {

    /**
     * 扫描指定目录下的场景配置文件
     *
     * @param scenariosDir 场景配置目录
     * @return 场景配置列表
     */
    List<ScenarioConfig> scan(Path scenariosDir);
}
