package com.smartkit.toolbox.manager;

import com.smartkit.toolbox.model.tool.ToolConfig;
import com.smartkit.toolbox.scanner.ToolScanner;
import com.smartkit.toolbox.scanner.impl.DirectoryToolScanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ToolManager 集成测试
 */
class ToolManagerTest {

    @TempDir
    Path tempDir;

    private ToolScanner scanner;
    private ToolManager toolManager;

    @BeforeEach
    void setUp() throws IOException {
        // 创建实际的工具目录
        Path toolsDir = tempDir.resolve("tools");
        Files.createDirectory(toolsDir);

        Path toolDir = toolsDir.resolve("test-tool");
        Files.createDirectory(toolDir);
        Files.writeString(toolDir.resolve("tool.json"),
            "{\"id\":\"test-tool\",\"name\":\"测试工具\",\"version\":\"1.0.0\",\"command\":{\"windows\":\"test.bat\"}}");

        scanner = new com.smartkit.toolbox.scanner.impl.DirectoryToolScanner(toolsDir);
        toolManager = new ToolManager(scanner);
    }

    @Test
    void testLoadTools() throws IOException {
        toolManager.loadTools();

        assertEquals(1, toolManager.getToolCount());
        assertTrue(toolManager.hasTool("test-tool"));
    }

    @Test
    void testListTools() throws IOException {
        toolManager.loadTools();

        List<ToolConfig> tools = toolManager.listTools();

        assertEquals(1, tools.size());
        assertEquals("test-tool", tools.get(0).getId());
    }

    @Test
    void testGetTool() throws IOException {
        toolManager.loadTools();

        Optional<ToolConfig> tool = toolManager.getTool("test-tool");

        assertTrue(tool.isPresent());
        assertEquals("测试工具", tool.get().getName());
    }

    @Test
    void testGetToolNotFound() throws IOException {
        toolManager.loadTools();

        Optional<ToolConfig> tool = toolManager.getTool("not-exist");

        assertTrue(tool.isEmpty());
    }

    @Test
    void testHasTool() throws IOException {
        toolManager.loadTools();

        assertTrue(toolManager.hasTool("test-tool"));
        assertFalse(toolManager.hasTool("not-exist"));
    }

    @Test
    void testReloadTools() throws IOException {
        toolManager.loadTools();
        assertEquals(1, toolManager.getToolCount());

        // 添加新工具
        Path newToolDir = tempDir.resolve("tools/new-tool");
        Files.createDirectory(newToolDir);
        Files.writeString(newToolDir.resolve("tool.json"),
            "{\"id\":\"new-tool\",\"name\":\"新工具\",\"version\":\"1.0.0\",\"command\":{\"windows\":\"new.bat\"}}");

        toolManager.loadTools();
        assertEquals(2, toolManager.getToolCount());
    }
}