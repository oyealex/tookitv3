package com.smartkit.toolbox.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DateTimeUtil 单元测试
 */
class DateTimeUtilTest {

    @Test
    void testGetDirTimestamp() {
        String timestamp = DateTimeUtil.getDirTimestamp();
        assertNotNull(timestamp);
        assertEquals(17, timestamp.length());
        assertTrue(timestamp.matches("\\d{17}"));
    }

    @Test
    void testFormatForDir() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 3, 23, 34, 1, 123000000);
        String result = DateTimeUtil.formatForDir(dateTime);
        assertEquals("20260303233401123", result);
    }

    @Test
    void testFormatForJson() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 3, 23, 34, 1, 123000000);
        String result = DateTimeUtil.formatForJson(dateTime);
        assertEquals("2026-03-03 23:34:01.123", result);
    }

    @Test
    void testFormatForJsonFromTimestamp() {
        // 使用 LocalDateTime 转换验证，不依赖时区
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 3, 23, 34, 1, 123000000);
        String expected = DateTimeUtil.formatForJson(dateTime);

        String result = DateTimeUtil.formatForJson(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        assertEquals(expected, result);
    }

    @Test
    void testParseJsonTime() {
        String timeStr = "2026-03-03 23:34:01.123";
        LocalDateTime result = DateTimeUtil.parseJsonTime(timeStr);
        assertNotNull(result);
        assertEquals(2026, result.getYear());
        assertEquals(3, result.getMonthValue());
        assertEquals(3, result.getDayOfMonth());
        assertEquals(23, result.getHour());
        assertEquals(34, result.getMinute());
        assertEquals(1, result.getSecond());
    }

    @Test
    void testRoundTrip() {
        LocalDateTime original = LocalDateTime.now();
        String json = DateTimeUtil.formatForJson(original);
        LocalDateTime parsed = DateTimeUtil.parseJsonTime(json);
        assertEquals(original.getYear(), parsed.getYear());
        assertEquals(original.getMonthValue(), parsed.getMonthValue());
        assertEquals(original.getDayOfMonth(), parsed.getDayOfMonth());
        assertEquals(original.getHour(), parsed.getHour());
        assertEquals(original.getMinute(), parsed.getMinute());
    }
}