package com.smartkit.toolbox.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ToolIdValidator 单元测试
 */
class ToolIdValidatorTest {

    @Test
    void testValidToolId() {
        assertTrue(ToolIdValidator.isValid("device-ping"));
        assertTrue(ToolIdValidator.isValid("tool_123"));
        assertTrue(ToolIdValidator.isValid("ABC"));
        assertTrue(ToolIdValidator.isValid("a".repeat(64)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a b", "tool@id", "工具", "a/b", "a\\b"})
    void testInvalidToolId(String toolId) {
        assertFalse(ToolIdValidator.isValid(toolId));
    }

    @Test
    void testToolIdTooLong() {
        assertFalse(ToolIdValidator.isValid("a".repeat(65)));
    }

    @Test
    void testNullToolId() {
        assertFalse(ToolIdValidator.isValid(null));
    }

    @Test
    void testValidateValidId() {
        assertNull(ToolIdValidator.validate("device-ping"));
    }

    @Test
    void testValidateNull() {
        assertNotNull(ToolIdValidator.validate(null));
    }

    @Test
    void testValidateTooLong() {
        String error = ToolIdValidator.validate("a".repeat(65));
        assertNotNull(error);
        assertTrue(error.contains("64"));
    }

    @Test
    void testValidateInvalidChars() {
        String error = ToolIdValidator.validate("tool id");
        assertNotNull(error);
        assertTrue(error.contains("下划线"));
    }
}