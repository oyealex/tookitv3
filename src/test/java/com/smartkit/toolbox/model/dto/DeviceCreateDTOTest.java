package com.smartkit.toolbox.model.dto;

import com.smartkit.toolbox.model.DeviceType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceCreateDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("验证成功 - 完整有效数据")
    void testValidCreate() {
        DeviceCreateDTO dto = new DeviceCreateDTO();
        dto.setIp("192.168.1.1");
        dto.setName("Test Device");
        dto.setType(DeviceType.STORAGE);
        dto.setModel("Model-X");
        dto.setVersion("v1.0");
        dto.setUsername("admin");
        dto.setPassword("password");

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("验证失败 - IP为空")
    void testMissingIp() {
        DeviceCreateDTO dto = new DeviceCreateDTO();
        dto.setName("Test Device");
        dto.setType(DeviceType.STORAGE);
        dto.setPassword("password");

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.ip.required}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证失败 - IP格式错误")
    void testInvalidIpFormat() {
        DeviceCreateDTO dto = new DeviceCreateDTO();
        dto.setIp("invalid-ip");
        dto.setName("Test Device");
        dto.setType(DeviceType.STORAGE);
        dto.setPassword("password");

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.ip.invalid}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证失败 - 类型为空")
    void testMissingType() {
        DeviceCreateDTO dto = new DeviceCreateDTO();
        dto.setIp("192.168.1.1");
        dto.setPassword("password");

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.type.required}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证失败 - 密码为空")
    void testMissingPassword() {
        DeviceCreateDTO dto = new DeviceCreateDTO();
        dto.setIp("192.168.1.1");
        dto.setType(DeviceType.STORAGE);

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.password.required}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证名称长度超限")
    void testNameTooLong() {
        DeviceCreateDTO dto = new DeviceCreateDTO();
        dto.setIp("192.168.1.1");
        dto.setType(DeviceType.STORAGE);
        dto.setPassword("password");
        dto.setName("a".repeat(121));

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.name.too.long}", violations.iterator().next().getMessage());
    }
}
