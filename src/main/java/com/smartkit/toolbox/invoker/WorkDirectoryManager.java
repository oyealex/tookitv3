package com.smartkit.toolbox.invoker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 工作目录管理类
 * 创建和管理子工具的工作目录 data/archive/{toolid}-{timestamp}
 */
@Component
public class WorkDirectoryManager {

    private static final Logger log = LoggerFactory.getLogger(WorkDirectoryManager.class);

    /**
     * 归档根目录
     */
    private final Path archiveRoot;

    public WorkDirectoryManager() {
        this(Paths.get("data/archive").toAbsolutePath().normalize());
    }

    public WorkDirectoryManager(Path archiveRoot) {
        this.archiveRoot = archiveRoot;
    }

    /**
     * 创建工作目录
     *
     * @param toolId    工具 ID
     * @param timestamp 时间戳（格式：20260303233450123）
     * @return 工作目录路径
     * @throws IOException 如果目录创建失败
     */
    public Path createWorkDirectory(String toolId, String timestamp) throws IOException {
        Path workDir = archiveRoot.resolve(toolId + "-" + timestamp);

        // 如果目录已存在，追加递增序号
        int counter = 1;
        while (Files.exists(workDir)) {
            workDir = archiveRoot.resolve(String.format("%s-%s-%d", toolId, timestamp, counter));
            counter++;
        }

        Files.createDirectories(workDir);
        log.info("创建工作目录: {}", workDir);

        return workDir;
    }

    /**
     * 获取归档根目录
     */
    public Path getArchiveRoot() {
        return archiveRoot;
    }

    /**
     * 列出所有历史工作目录
     *
     * @return 工作目录列表（按修改时间倒序）
     */
    public java.util.List<Path> listWorkDirectories() throws IOException {
        if (!Files.exists(archiveRoot)) {
            return java.util.Collections.emptyList();
        }

        try (var entries = Files.list(archiveRoot)) {
            return entries
                    .filter(Files::isDirectory)
                    .sorted((a, b) -> {
                        try {
                            return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .toList();
        }
    }

    /**
     * 解析工作目录获取工具 ID 和时间戳
     *
     * @param workDir 工作目录路径
     * @return 解析结果，包含 toolId 和 timestamp，解析失败返回 null
     */
    public static WorkDirInfo parseWorkDirectory(Path workDir) {
        String dirName = workDir.getFileName().toString();

        // 格式：{toolid}-{timestamp}[-{counter}]
        // 时间戳是 17 位数字，精确到毫秒
        // 找到最后一个 17 位数字序列作为时间戳

        int timestampStart = -1;
        for (int i = dirName.length() - 1; i >= 16; i--) {
            String potentialTimestamp = dirName.substring(i - 16, i + 1);
            if (potentialTimestamp.matches("\\d{17}")) {
                timestampStart = i - 16;
                break;
            }
        }

        if (timestampStart <= 0) {
            return null;
        }

        String timestamp = dirName.substring(timestampStart, timestampStart + 17);
        String toolId = dirName.substring(0, timestampStart - 1);  // 减去分隔符

        return new WorkDirInfo(toolId, timestamp, dirName);
    }

    /**
     * 工作目录信息
     */
    public record WorkDirInfo(String toolId, String timestamp, String dirName) {
    }
}