package com.smartkit.toolbox.util;

import java.util.regex.Pattern;

/**
 * IP地址验证工具类，提供IPv4地址格式验证功能。
 *
 * @author SmartKit
 * @since 1.0.0
 */
public final class IpValidator {

    /**
     * IPv4地址正则表达式模式
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$"
    );

    /**
     * 私有构造函数，防止实例化
     */
    private IpValidator() {
    }

    /**
     * 验证IPv4地址格式是否有效
     *
     * @param ip IP地址字符串
     * @return 是否有效
     */
    public static boolean isValid(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip).matches();
    }
}