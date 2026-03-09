package com.smartkit.toolbox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartkit.toolbox.model.DeviceType;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.model.dto.DeviceUpdateDTO;
import com.smartkit.toolbox.repository.DeviceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 设备控制器集成测试
 * 使用 @BeforeEach/@AfterEach 清理数据库确保测试隔离
 * 使用 @TestInstance(PER_METHOD) 确保每个测试创建新的实例
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class DeviceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    private static final AtomicInteger ipCounter = new AtomicInteger(1);

    private DeviceCreateDTO validDTO;

    @BeforeEach
    void setUp() {
        // 清理数据库确保测试隔离
        try {
            deviceRepository.deleteAll();
        } catch (Exception e) {
            // 忽略清理错误
        }

        validDTO = new DeviceCreateDTO();
        validDTO.setIp("192.168.100.1");
        validDTO.setName("Test Device");
        validDTO.setType(DeviceType.STORAGE);
        validDTO.setModel("Model-X");
        validDTO.setVersion("v1.0");
        validDTO.setUsername("admin");
        validDTO.setPassword("password");
    }

    @AfterEach
    void tearDown() {
        // 测试完成后清理数据
        try {
            deviceRepository.deleteAll();
        } catch (Exception e) {
            // 忽略清理错误
        }
    }

    @Test
    @DisplayName("完整流程测试：添加->查询->更新->删除")
    void fullLifecycle_Success() throws Exception {
        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.ip").value("192.168.100.1"));

        mockMvc.perform(get("/api/v1/devices/192.168.100.1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Test Device"));

        DeviceUpdateDTO updateDTO = new DeviceUpdateDTO();
        updateDTO.setName("Updated Device");

        mockMvc.perform(put("/api/v1/devices/192.168.100.1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/devices/192.168.100.1"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("分页查询测试")
    void pagination_Success() throws Exception {
        for (int i = 1; i <= 5; i++) {
            DeviceCreateDTO dto = new DeviceCreateDTO();
            dto.setIp("192.168.101." + i);  // 使用独立的IP地址段
            dto.setType(DeviceType.SERVER);
            dto.setPassword("pass");
            mockMvc.perform(post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
        }

        // 验证分页返回正确的记录数
        mockMvc.perform(get("/api/v1/devices")
                .param("offset", "0")
                .param("limit", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.list", hasSize(3)));
    }

    @Test
    @DisplayName("条件筛选测试")
    void filter_Success() throws Exception {
        DeviceCreateDTO dto1 = new DeviceCreateDTO();
        dto1.setIp("10.1.0.1");  // 使用独立的IP地址段
        dto1.setType(DeviceType.STORAGE);
        dto1.setPassword("pass");
        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto1)))
            .andExpect(status().isOk());

        DeviceCreateDTO dto2 = new DeviceCreateDTO();
        dto2.setIp("10.1.0.2");  // 使用独立的IP地址段
        dto2.setType(DeviceType.SERVER);
        dto2.setPassword("pass");
        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/devices")
                .param("type", "STORAGE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.total").value(1))
            .andExpect(jsonPath("$.data.list[0].type").value("STORAGE"));
    }

    @Test
    @DisplayName("批量操作测试")
    void batchOperations_Success() throws Exception {
        String batchJson = """
            [
                {"ip":"192.168.102.1","type":"STORAGE","password":"pass"},
                {"ip":"192.168.102.2","type":"SERVER","password":"pass"}
            ]
            """;

        mockMvc.perform(post("/api/v1/devices/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(batchJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.successCount").value(2));

        mockMvc.perform(delete("/api/v1/devices/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"192.168.102.1\",\"192.168.102.2\"]"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.successCount").value(2));
    }
}