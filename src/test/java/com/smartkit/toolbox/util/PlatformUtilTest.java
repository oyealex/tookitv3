package com.smartkit.toolbox.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PlatformUtil 单元测试
 */
class PlatformUtilTest {

    @Test
    void testGetPlatformName() {
        String platform = PlatformUtil.getPlatformName();
        assertNotNull(platform);
        assertFalse(platform.isEmpty());
    }

    @Test
    void testIsWindowsOrNot() {
        // 至少有一个为真
        assertTrue(PlatformUtil.isWindows() || PlatformUtil.isLinux() || PlatformUtil.isMac());
    }
}