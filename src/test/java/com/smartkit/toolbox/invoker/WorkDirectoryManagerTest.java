package com.smartkit.toolbox.invoker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WorkDirectoryManager 单元测试
 */
class WorkDirectoryManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void testCreateWorkDirectory() throws IOException {
        WorkDirectoryManager manager = new WorkDirectoryManager(tempDir);

        Path workDir = manager.createWorkDirectory("device-ping", "20260303233401123");

        assertTrue(Files.exists(workDir));
        assertTrue(workDir.toString().contains("device-ping"));
        assertTrue(workDir.toString().contains("20260303233401123"));
    }

    @Test
    void testCreateWorkDirectoryWithDuplicate() throws IOException {
        WorkDirectoryManager manager = new WorkDirectoryManager(tempDir);

        // 创建第一个目录
        Path dir1 = manager.createWorkDirectory("device-ping", "20260303233401123");
        assertTrue(Files.exists(dir1));

        // 创建第二个同名目录（应该追加序号）
        Path dir2 = manager.createWorkDirectory("device-ping", "20260303233401123");
        assertTrue(Files.exists(dir2));
        assertTrue(dir2.toString().contains("device-ping-20260303233401123-1"));
    }

    @Test
    void testListWorkDirectories() throws IOException {
        WorkDirectoryManager manager = new WorkDirectoryManager(tempDir);

        // 创建测试目录
        manager.createWorkDirectory("tool-a", "20260303233401123");
        manager.createWorkDirectory("tool-b", "20260303233401123");

        List<Path> dirs = manager.listWorkDirectories();

        assertEquals(2, dirs.size());
    }

    @Test
    void testParseWorkDirectory() {
        // 测试解析工具目录名
        String dirName = "device-ping-20260303233401123";
        Path path = tempDir.resolve(dirName);

        WorkDirectoryManager.WorkDirInfo info = WorkDirectoryManager.parseWorkDirectory(path);

        assertNotNull(info);
        assertEquals("device-ping", info.toolId());
        assertEquals("20260303233401123", info.timestamp());
    }

    @Test
    void testParseWorkDirectoryWithCounter() {
        String dirName = "device-ping-20260303233401123-1";
        Path path = tempDir.resolve(dirName);

        WorkDirectoryManager.WorkDirInfo info = WorkDirectoryManager.parseWorkDirectory(path);

        assertNotNull(info);
        assertEquals("device-ping", info.toolId());
    }

    @Test
    void testParseInvalidWorkDirectory() {
        Path path = tempDir.resolve("invalid-name");

        WorkDirectoryManager.WorkDirInfo info = WorkDirectoryManager.parseWorkDirectory(path);

        assertNull(info);
    }
}