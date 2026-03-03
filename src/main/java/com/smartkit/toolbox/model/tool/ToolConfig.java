package com.smartkit.toolbox.model.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.IOException;

/**
 * 工具配置实体类，从 tool.json 文件加载
 */
@Data
public class ToolConfig {

    /**
     * 工具唯一标识（英文、数字、下划线、短横线组成，不超过64字符）
     */
    private String id;

    /**
     * 工具显示名称
     */
    private String name;

    /**
     * 工具版本号
     */
    private String version;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 启动命令
     * 支持字符串形式（所有平台共用）或对象形式（区分 windows/linux）
     */
    @JsonDeserialize(using = CommandConfigDeserializer.class)
    private CommandConfig command;

    /**
     * 命令配置
     */
    @Data
    public static class CommandConfig {
        /**
         * Windows 平台命令
         */
        @JsonProperty("windows")
        private String windows;

        /**
         * Linux 平台命令
         */
        @JsonProperty("linux")
        private String linux;

        /**
         * 通用命令（字符串形式）
         */
        private String common;

        /**
         * 判断是否为对象形式（区分平台）
         */
        public boolean isPlatformSpecific() {
            return windows != null || linux != null;
        }

        /**
         * 获取当前平台适用的命令
         */
        public String getCommand() {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                return windows != null ? windows : common;
            } else {
                return linux != null ? linux : common;
            }
        }
    }

    /**
     * 命令配置反序列化器，支持字符串和对象两种形式
     */
    public static class CommandConfigDeserializer extends JsonDeserializer<CommandConfig> {
        @Override
        public CommandConfig deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            CommandConfig config = new CommandConfig();

            if (p.currentToken().isScalarValue()) {
                // 字符串形式，所有平台共用同一命令
                config.setCommon(p.getValueAsString());
            } else {
                // 对象形式，解析 windows 和 linux
                config = p.readValueAs(CommandConfig.class);
            }

            return config;
        }
    }
}