package com.smartkit.toolbox.util;

import java.util.regex.Pattern;

/**
 * 工具 ID 验证工具类
 */
public final class ToolIdValidator {

    /**
     * 工具 ID 正则：英文、数字、下划线、短横线，1-64 字符
     */
    private static final Pattern TOOL_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,64}$");

    private ToolIdValidator() {
        // 工具类不允许实例化
    }

    /**
     * 验证工具 ID 是否符合规范
     *
     * @param toolId 工具 ID
     * @return true-符合规范，false-不符合
     */
    public static boolean isValid(String toolId) {
        return toolId != null && TOOL_ID_PATTERN.matcher(toolId).matches();
    }

    /**
     * 验证工具 ID 并返回验证结果
     *
     * @param toolId 工具 ID
     * @return 验证结果，null 表示通过，否则返回错误信息
     */
    public static String validate(String toolId) {
        if (toolId == null) {
            return "工具 ID 不能为空";
        }
        if (toolId.length() > 64) {
            return "工具 ID 长度不能超过 64 字符";
        }
        if (!TOOL_ID_PATTERN.matcher(toolId).matches()) {
            return "工具 ID 只能包含英文、数字、下划线和短横线";
        }
        return null;
    }
}