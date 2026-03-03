package com.smartkit.toolbox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.DeviceType;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.model.dto.DeviceUpdateDTO;
import com.smartkit.toolbox.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    private DeviceCreateDTO validDTO;

    @BeforeEach
    void setUp() {
        // Clear database before each test by deleting all devices
        try {
            List<Device> existingDevices = deviceRepository.findAll(0, 1000, null, null, null, null, null);
            if (!existingDevices.isEmpty()) {
                List<String> ips = existingDevices.stream()
                    .map(Device::getIp)
                    .toList();
                deviceRepository.deleteByIps(ips);
            }
        } catch (Exception e) {
            // Ignore errors during cleanup
        }
        
        validDTO = new DeviceCreateDTO();
        validDTO.setIp("192.168.1.1");
        validDTO.setName("Test Device");
        validDTO.setType(DeviceType.STORAGE);
        validDTO.setModel("Model-X");
        validDTO.setVersion("v1.0");
        validDTO.setUsername("admin");
        validDTO.setPassword("password");
    }

    @Test
    @DisplayName("完整流程测试：添加->查询->更新->删除")
    void fullLifecycle_Success() throws Exception {
        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.ip").value("192.168.1.1"));

        mockMvc.perform(get("/api/v1/devices/192.168.1.1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Test Device"));

        DeviceUpdateDTO updateDTO = new DeviceUpdateDTO();
        updateDTO.setName("Updated Device");

        mockMvc.perform(put("/api/v1/devices/192.168.1.1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/devices/192.168.1.1"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("分页查询测试")
    void pagination_Success() throws Exception {
        for (int i = 1; i <= 5; i++) {
            DeviceCreateDTO dto = new DeviceCreateDTO();
            dto.setIp("192.168.1." + i);
            dto.setType(DeviceType.SERVER);
            dto.setPassword("pass");
            mockMvc.perform(post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/v1/devices")
                .param("offset", "0")
                .param("limit", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.total").value(5))
            .andExpect(jsonPath("$.data.list", hasSize(3)));
    }

    @Test
    @DisplayName("条件筛选测试")
    void filter_Success() throws Exception {
        DeviceCreateDTO dto1 = new DeviceCreateDTO();
        dto1.setIp("10.0.0.1");
        dto1.setType(DeviceType.STORAGE);
        dto1.setPassword("pass");
        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto1)))
            .andExpect(status().isOk());

        DeviceCreateDTO dto2 = new DeviceCreateDTO();
        dto2.setIp("10.0.0.2");
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
                {"ip":"192.168.2.1","type":"STORAGE","password":"pass"},
                {"ip":"192.168.2.2","type":"SERVER","password":"pass"}
            ]
            """;

        mockMvc.perform(post("/api/v1/devices/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(batchJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.successCount").value(2));

        mockMvc.perform(delete("/api/v1/devices/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[\"192.168.2.1\",\"192.168.2.2\"]"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.successCount").value(2));
    }
}