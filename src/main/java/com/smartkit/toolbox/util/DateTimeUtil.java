package com.smartkit.toolbox.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间格式化工具类
 */
public final class DateTimeUtil {

    /**
     * 目录名时间格式：20260303233450123（17位，精确到毫秒）
     */
    public static final String DIR_TIMESTAMP_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * JSON 时间格式：2026-03-03 23:34:01.123
     */
    public static final String JSON_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private static final DateTimeFormatter DIR_FORMATTER = DateTimeFormatter.ofPattern(DIR_TIMESTAMP_FORMAT);
    private static final DateTimeFormatter JSON_FORMATTER = DateTimeFormatter.ofPattern(JSON_TIME_FORMAT);

    private DateTimeUtil() {
        // 工具类不允许实例化
    }

    /**
     * 获取当前时间的时间戳（用于目录名）
     *
     * @return 格式：20260303233450123
     */
    public static String getDirTimestamp() {
        return LocalDateTime.now().format(DIR_FORMATTER);
    }

    /**
     * 格式化时间为目录名格式
     *
     * @param dateTime 日期时间
     * @return 格式：20260303233450123
     */
    public static String formatForDir(LocalDateTime dateTime) {
        return dateTime.format(DIR_FORMATTER);
    }

    /**
     * 格式化时间为 JSON 格式
     *
     * @param dateTime 日期时间
     * @return 格式：2026-03-03 23:34:01.123
     */
    public static String formatForJson(LocalDateTime dateTime) {
        return dateTime.format(JSON_FORMATTER);
    }

    /**
     * 格式化时间为 JSON 格式（从毫秒时间戳）
     *
     * @param timestamp 毫秒时间戳
     * @return 格式：2026-03-03 23:34:01.123
     */
    public static String formatForJson(long timestamp) {
        return formatForJson(LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp),
                java.time.ZoneId.systemDefault()
        ));
    }

    /**
     * 解析 JSON 格式时间
     *
     * @param timeStr 格式：2026-03-03 23:34:01.123
     * @return 日期时间
     */
    public static LocalDateTime parseJsonTime(String timeStr) {
        return LocalDateTime.parse(timeStr, JSON_FORMATTER);
    }
}