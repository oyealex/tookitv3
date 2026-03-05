package com.smartkit.toolbox.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceQueryDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("验证成功 - 有效参数")
    void testValidQuery() {
        DeviceQueryDTO query = new DeviceQueryDTO();
        query.setOffset(0);
        query.setLimit(20);

        var violations = validator.validate(query);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("验证失败 - offset为负数")
    void testInvalidOffset() {
        DeviceQueryDTO query = new DeviceQueryDTO();
        query.setOffset(-1);

        var violations = validator.validate(query);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.query.offset.invalid}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证失败 - limit小于1")
    void testInvalidLimitMin() {
        DeviceQueryDTO query = new DeviceQueryDTO();
        query.setOffset(0);
        query.setLimit(0);

        var violations = validator.validate(query);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.query.limit.min}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证失败 - limit大于100")
    void testInvalidLimitMax() {
        DeviceQueryDTO query = new DeviceQueryDTO();
        query.setOffset(0);
        query.setLimit(101);

        var violations = validator.validate(query);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("{error.device.query.limit.exceeded}", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("验证默认值")
    void testDefaultValues() {
        DeviceQueryDTO query = new DeviceQueryDTO();

        assertEquals(0, query.getOffset());
        assertEquals(20, query.getLimit());
    }
}
