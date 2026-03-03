package com.smartkit.toolbox.scanner;

import com.smartkit.toolbox.model.tool.ToolConfig;
import com.smartkit.toolbox.scanner.impl.DirectoryToolScanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ToolScanner 集成测试
 */
class ToolScannerIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void testScanEmptyDirectory() throws IOException {
        // 创建空的 tools 目录
        Path toolsDir = tempDir.resolve("tools");
        Files.createDirectory(toolsDir);

        DirectoryToolScanner scanner = new DirectoryToolScanner(toolsDir);
        List<ToolConfig> tools = scanner.scan();

        assertNotNull(tools);
        assertTrue(tools.isEmpty());
    }

    @Test
    void testScanValidTool() throws IOException {
        // 创建工具目录结构
        Path toolsDir = tempDir.resolve("tools");
        Files.createDirectory(toolsDir);

        Path toolDir = toolsDir.resolve("device-ping");
        Files.createDirectory(toolDir);

        // 创建 tool.json - 使用对象形式的 command
        String toolJson = """
            {
                "id": "device-ping",
                "name": "设备Ping工具",
                "version": "1.0.0",
                "command": {
                    "windows": "ping.bat",
                    "linux": "ping.sh"
                }
            }
            """;
        Files.writeString(toolDir.resolve("tool.json"), toolJson);

        // 扫描
        DirectoryToolScanner scanner = new DirectoryToolScanner(toolsDir);
        List<ToolConfig> tools = scanner.scan();

        assertEquals(1, tools.size());
        assertEquals("device-ping", tools.get(0).getId());
        assertEquals("设备Ping工具", tools.get(0).getName());
    }

    @Test
    void testScanMultipleTools() throws IOException {
        // 创建多个工具目录
        Path toolsDir = tempDir.resolve("tools");
        Files.createDirectory(toolsDir);

        // 工具1
        Path tool1Dir = toolsDir.resolve("tool-a");
        Files.createDirectory(tool1Dir);
        Files.writeString(tool1Dir.resolve("tool.json"),
            "{\"id\":\"tool-a\",\"name\":\"工具A\",\"version\":\"1.0.0\",\"command\":{\"windows\":\"a.bat\",\"linux\":\"a.sh\"}}");

        // 工具2
        Path tool2Dir = toolsDir.resolve("tool-b");
        Files.createDirectory(tool2Dir);
        Files.writeString(tool2Dir.resolve("tool.json"),
            "{\"id\":\"tool-b\",\"name\":\"工具B\",\"version\":\"2.0.0\",\"command\":{\"windows\":\"b.bat\",\"linux\":\"b.sh\"}}");

        DirectoryToolScanner scanner = new DirectoryToolScanner(toolsDir);
        List<ToolConfig> tools = scanner.scan();

        assertEquals(2, tools.size());
    }

    @Test
    void testScanSkipsInvalidToolId() throws IOException {
        Path toolsDir = tempDir.resolve("tools");
        Files.createDirectory(toolsDir);

        // 创建无效目录名（包含空格）
        Path invalidDir = toolsDir.resolve("invalid tool");
        Files.createDirectory(invalidDir);
        Files.writeString(invalidDir.resolve("tool.json"),
            "{\"id\":\"invalid tool\",\"name\":\"无效工具\",\"version\":\"1.0.0\",\"command\":{\"windows\":\"test.bat\"}}");

        DirectoryToolScanner scanner = new DirectoryToolScanner(toolsDir);
        List<ToolConfig> tools = scanner.scan();

        assertTrue(tools.isEmpty());
    }

    @Test
    void testScanSkipsMissingConfig() throws IOException {
        Path toolsDir = tempDir.resolve("tools");
        Files.createDirectory(toolsDir);

        // 创建没有 tool.json 的目录
        Path toolDir = toolsDir.resolve("no-config");
        Files.createDirectory(toolDir);

        DirectoryToolScanner scanner = new DirectoryToolScanner(toolsDir);
        List<ToolConfig> tools = scanner.scan();

        assertTrue(tools.isEmpty());
    }

    @Test
    void testGetTool() throws IOException {
        Path toolsDir = tempDir.resolve("tools");
        Files.createDirectory(toolsDir);

        Path toolDir = toolsDir.resolve("device-ping");
        Files.createDirectory(toolDir);
        Files.writeString(toolDir.resolve("tool.json"),
            "{\"id\":\"device-ping\",\"name\":\"设备Ping工具\",\"version\":\"1.0.0\",\"command\":{\"windows\":\"ping.bat\"}}");

        DirectoryToolScanner scanner = new DirectoryToolScanner(toolsDir);
        scanner.scan();

        var tool = scanner.getTool("device-ping");
        assertTrue(tool.isPresent());
        assertEquals("device-ping", tool.get().getId());

        var notFound = scanner.getTool("not-exist");
        assertTrue(notFound.isEmpty());
    }
}