package com.smartkit.toolbox.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class IpValidatorTest {

    @Test
    @DisplayName("有效IPv4地址")
    void isValid_ValidIp() {
        assertTrue(IpValidator.isValid("192.168.1.1"));
        assertTrue(IpValidator.isValid("10.0.0.1"));
        assertTrue(IpValidator.isValid("255.255.255.255"));
        assertTrue(IpValidator.isValid("0.0.0.0"));
        assertTrue(IpValidator.isValid("127.0.0.1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "256.1.1.1",
        "1.256.1.1",
        "1.1.256.1",
        "1.1.1.256",
        "1.1.1",
        "1.1.1.1.1",
        "a.b.c.d",
        "192.168.1",
        "192.168.1.1.1",
        "",
        "   "
    })
    @DisplayName("无效IPv4地址")
    void isValid_InvalidIp(String ip) {
        assertFalse(IpValidator.isValid(ip));
    }

    @Test
    @DisplayName("null IP地址")
    void isValid_NullIp() {
        assertFalse(IpValidator.isValid(null));
    }

    @Test
    @DisplayName("边界值 - 最小值")
    void isValid_MinValue() {
        assertTrue(IpValidator.isValid("0.0.0.0"));
    }

    @Test
    @DisplayName("边界值 - 最大值")
    void isValid_MaxValue() {
        assertTrue(IpValidator.isValid("255.255.255.255"));
    }

    @Test
    @DisplayName("边界值 - 边界+1")
    void isValid_ExceedMaxValue() {
        assertFalse(IpValidator.isValid("256.0.0.0"));
    }
}