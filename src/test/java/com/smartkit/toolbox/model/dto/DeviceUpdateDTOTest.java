package com.smartkit.toolbox.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceUpdateDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("验证成功 - 空对象（所有字段可选）")
    void testEmptyUpdate() {
        DeviceUpdateDTO dto = new DeviceUpdateDTO();

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("验证成功 - 有效数据")
    void testValidUpdate() {
        DeviceUpdateDTO dto = new DeviceUpdateDTO();
        dto.setName("Updated Name");
        dto.setModel("Model-X");
        dto.setVersion("v1.0");

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("验证名称长度超限")
    void testNameTooLong() {
        DeviceUpdateDTO dto = new DeviceUpdateDTO();
        dto.setName("a".repeat(121));

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.name.too.long}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证型号长度超限")
    void testModelTooLong() {
        DeviceUpdateDTO dto = new DeviceUpdateDTO();
        dto.setModel("a".repeat(101));

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.model.too.long}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证版本长度超限")
    void testVersionTooLong() {
        DeviceUpdateDTO dto = new DeviceUpdateDTO();
        dto.setVersion("a".repeat(51));

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.version.too.long}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证用户名长度超限")
    void testUsernameTooLong() {
        DeviceUpdateDTO dto = new DeviceUpdateDTO();
        dto.setUsername("a".repeat(101));

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.username.too.long}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证密码长度超限")
    void testPasswordTooLong() {
        DeviceUpdateDTO dto = new DeviceUpdateDTO();
        dto.setPassword("a".repeat(256));

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.password.too.long}", violations.iterator().next().getMessage());
    }
}
