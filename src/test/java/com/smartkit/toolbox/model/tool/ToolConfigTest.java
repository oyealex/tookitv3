package com.smartkit.toolbox.model.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ToolConfig 单元测试
 */
class ToolConfigTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDeserializeCommandAsString() throws Exception {
        String json = "{\"id\":\"tool-a\",\"name\":\"工具A\",\"version\":\"1.0.0\",\"command\":\"script.bat\"}";
        ToolConfig config = objectMapper.readValue(json, ToolConfig.class);

        assertEquals("tool-a", config.getId());
        assertEquals("工具A", config.getName());
        assertEquals("1.0.0", config.getVersion());
        assertNotNull(config.getCommand());
        assertNotNull(config.getCommand().getCommand());
    }

    @Test
    void testDeserializeCommandAsObject() throws Exception {
        String json = "{\"id\":\"tool-b\",\"name\":\"工具B\",\"version\":\"1.0.0\",\"command\":{\"windows\":\"run.bat\",\"linux\":\"run.sh\"}}";
        ToolConfig config = objectMapper.readValue(json, ToolConfig.class);

        assertEquals("tool-b", config.getId());
        assertNotNull(config.getCommand());
        assertEquals("run.bat", config.getCommand().getWindows());
        assertEquals("run.sh", config.getCommand().getLinux());
    }

    @Test
    void testCommandIsPlatformSpecific() {
        ToolConfig.CommandConfig cmd = new ToolConfig.CommandConfig();
        assertFalse(cmd.isPlatformSpecific());

        cmd.setWindows("a.bat");
        assertTrue(cmd.isPlatformSpecific());

        ToolConfig.CommandConfig cmd2 = new ToolConfig.CommandConfig();
        cmd2.setLinux("a.sh");
        assertTrue(cmd2.isPlatformSpecific());
    }

    @Test
    void testCommandGetCommand() {
        ToolConfig.CommandConfig cmd = new ToolConfig.CommandConfig();
        cmd.setWindows("win.bat");
        cmd.setLinux("lin.sh");
        cmd.setCommon("common.sh");

        String os = System.getProperty("os.name").toLowerCase();
        String result = cmd.getCommand();

        if (os.contains("win")) {
            assertEquals("win.bat", result);
        } else {
            assertEquals("lin.sh", result);
        }
    }

    @Test
    void testCommandCommonFallback() {
        ToolConfig.CommandConfig cmd = new ToolConfig.CommandConfig();
        cmd.setCommon("script.sh");

        // 在没有平台特定命令时使用 common
        assertEquals("script.sh", cmd.getCommand());
    }
}