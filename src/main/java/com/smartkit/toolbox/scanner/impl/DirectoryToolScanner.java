package com.smartkit.toolbox.scanner.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartkit.toolbox.model.tool.ToolConfig;
import com.smartkit.toolbox.scanner.ToolScanner;
import com.smartkit.toolbox.util.ToolIdValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 目录工具扫描器实现
 * 扫描 tools 目录下的一级子目录，发现子工具
 */
@Component
public class DirectoryToolScanner implements ToolScanner {

    private static final Logger log = LoggerFactory.getLogger(DirectoryToolScanner.class);

    /**
     * 工具配置文件名称
     */
    private static final String TOOL_CONFIG_FILE = "tool.json";

    /**
     * 默认工具目录
     */
    private static final String DEFAULT_TOOLS_DIR = "tools";

    private final Path toolsDirectory;
    private final ObjectMapper objectMapper;
    private final Map<String, ToolConfig> toolsCache = new HashMap<>();

    public DirectoryToolScanner() {
        this(Paths.get(DEFAULT_TOOLS_DIR).toAbsolutePath().normalize());
    }

    public DirectoryToolScanner(Path toolsDirectory) {
        this.toolsDirectory = toolsDirectory;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<ToolConfig> scan() throws IOException {
        log.info("开始扫描工具目录: {}", toolsDirectory);

        // 清理缓存，重新扫描
        toolsCache.clear();

        // 确保目录存在
        if (!Files.exists(toolsDirectory)) {
            log.warn("工具目录不存在: {}，将创建默认目录", toolsDirectory);
            Files.createDirectories(toolsDirectory);
            return Collections.emptyList();
        }

        if (!Files.isDirectory(toolsDirectory)) {
            log.error("工具路径不是目录: {}", toolsDirectory);
            throw new IOException("工具路径不是有效目录: " + toolsDirectory);
        }

        // 扫描一级子目录
        List<ToolConfig> tools = new ArrayList<>();
        try (var entries = Files.list(toolsDirectory)) {
            List<Path> subDirs = entries
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());

            log.info("发现 {} 个子目录", subDirs.size());

            for (Path subDir : subDirs) {
                String dirName = subDir.getFileName().toString();

                // 验证目录名（工具 ID）是否符合规范
                String validationError = ToolIdValidator.validate(dirName);
                if (validationError != null) {
                    log.warn("跳过无效工具目录 [{}]: {}", dirName, validationError);
                    continue;
                }

                // 检查是否包含 tool.json
                Path configFile = subDir.resolve(TOOL_CONFIG_FILE);
                if (!Files.exists(configFile)) {
                    log.warn("跳过工具目录 [{}]: 缺少配置文件 {}", dirName, TOOL_CONFIG_FILE);
                    continue;
                }

                // 解析 tool.json
                try {
                    ToolConfig config = parseToolConfig(configFile, dirName);
                    if (config != null) {
                        // 检查是否已存在相同 ID 的工具
                        if (toolsCache.containsKey(dirName)) {
                            log.warn("跳过重复的工具 ID: {}", dirName);
                            continue;
                        }

                        toolsCache.put(dirName, config);
                        tools.add(config);
                        log.info("成功加载工具: id={}, name={}, version={}",
                                config.getId(), config.getName(), config.getVersion());
                    }
                } catch (Exception e) {
                    log.error("解析工具配置失败 [{}]: {}", dirName, e.getMessage());
                }
            }
        }

        log.info("工具扫描完成，共发现 {} 个可用工具", tools.size());
        return tools;
    }

    @Override
    public Optional<ToolConfig> getTool(String toolId) {
        return Optional.ofNullable(toolsCache.get(toolId));
    }

    /**
     * 解析工具配置文件
     */
    private ToolConfig parseToolConfig(Path configFile, String dirName) throws IOException {
        ToolConfig config = objectMapper.readValue(configFile.toFile(), ToolConfig.class);

        // 验证必需字段
        if (config.getId() == null) {
            config.setId(dirName);
        }
        if (config.getName() == null) {
            log.warn("工具 [{}] 缺少 name 字段，使用目录名", dirName);
            config.setName(dirName);
        }
        if (config.getVersion() == null) {
            log.warn("工具 [{}] 缺少 version 字段，使用默认值 1.0.0", dirName);
            config.setVersion("1.0.0");
        }
        if (config.getCommand() == null) {
            log.error("工具 [{}] 缺少 command 字段", dirName);
            return null;
        }

        // 兼容字符串形式的 command
        if (config.getCommand().isPlatformSpecific()) {
            // 对象形式，已正确解析
        } else if (config.getCommand().getWindows() == null && config.getCommand().getLinux() == null) {
            // 尝试解析为字符串形式
            log.warn("tool.json 中 command 字段格式不正确");
            return null;
        }

        return config;
    }
}