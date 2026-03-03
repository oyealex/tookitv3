package com.smartkit.toolbox.util;

/**
 * 平台判断工具类
 */
public final class PlatformUtil {

    private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase();

    /**
     * 判断是否为 Windows 系统
     */
    public static boolean isWindows() {
        return OS_NAME.contains("win");
    }

    /**
     * 判断是否为 Linux 系统
     */
    public static boolean isLinux() {
        return OS_NAME.contains("linux") || OS_NAME.contains("unix");
    }

    /**
     * 判断是否为 macOS 系统
     */
    public static boolean isMac() {
        return OS_NAME.contains("mac");
    }

    /**
     * 获取当前平台名称
     */
    public static String getPlatformName() {
        if (isWindows()) {
            return "Windows";
        } else if (isLinux()) {
            return "Linux";
        } else if (isMac()) {
            return "Mac";
        }
        return "Unknown";
    }

    private PlatformUtil() {
        // 工具类不允许实例化
    }
}