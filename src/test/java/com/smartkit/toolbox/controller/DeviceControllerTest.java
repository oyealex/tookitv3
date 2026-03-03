package com.smartkit.toolbox.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.DeviceType;
import com.smartkit.toolbox.model.dto.BatchResultDTO;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.model.dto.DeviceQueryDTO;
import com.smartkit.toolbox.model.dto.DeviceUpdateDTO;
import com.smartkit.toolbox.repository.DeviceRepository;
import com.smartkit.toolbox.repository.OperationLogRepository;
import com.smartkit.toolbox.service.DeviceExcelService;
import com.smartkit.toolbox.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeviceService deviceService;

    @MockBean
    private DeviceExcelService deviceExcelService;

    @MockBean
    private DeviceRepository deviceRepository;

    @MockBean
    private OperationLogRepository operationLogRepository;

    private DeviceCreateDTO validDTO;
    private Device testDevice;

    @BeforeEach
    void setUp() {
        validDTO = new DeviceCreateDTO();
        validDTO.setIp("192.168.1.1");
        validDTO.setName("Test Device");
        validDTO.setType(DeviceType.STORAGE);
        validDTO.setModel("Model-X");
        validDTO.setVersion("v1.0");
        validDTO.setUsername("admin");
        validDTO.setPassword("password");

        testDevice = new Device();
        testDevice.setIp("192.168.1.1");
        testDevice.setName("Test Device");
        testDevice.setType(DeviceType.STORAGE);
    }

    @Test
    @DisplayName("POST /api/v1/devices - 添加设备成功")
    void addDevice_Success() throws Exception {
        when(deviceService.addDevice(any())).thenReturn(testDevice);

        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.ip").value("192.168.1.1"));
    }

    @Test
    @DisplayName("POST /api/v1/devices - 缺少必填字段")
    void addDevice_MissingRequiredField() throws Exception {
        validDTO.setIp(null);

        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/devices/batch - 批量添加成功")
    void addDevices_Success() throws Exception {
        BatchResultDTO result = new BatchResultDTO(1, 0, Collections.emptyList());
        when(deviceService.addDevices(anyList())).thenReturn(result);

        mockMvc.perform(post("/api/v1/devices/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(validDTO))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.successCount").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/devices/{ip} - 查询设备成功")
    void getDevice_Success() throws Exception {
        when(deviceService.getDevice("192.168.1.1")).thenReturn(testDevice);

        mockMvc.perform(get("/api/v1/devices/192.168.1.1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.ip").value("192.168.1.1"));
    }

    @Test
    @DisplayName("GET /api/v1/devices - 查询设备列表")
    void getDevices_Success() throws Exception {
        when(deviceService.getDevices(any())).thenReturn(List.of(testDevice));
        when(deviceService.count(any())).thenReturn(1L);

        mockMvc.perform(get("/api/v1/devices")
                .param("offset", "0")
                .param("limit", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/devices/{ip} - 更新设备成功")
    void updateDevice_Success() throws Exception {
        DeviceUpdateDTO updateDTO = new DeviceUpdateDTO();
        updateDTO.setName("Updated Name");
        
        testDevice.setName("Updated Name");
        when(deviceService.updateDevice(any(), any())).thenReturn(testDevice);

        mockMvc.perform(put("/api/v1/devices/192.168.1.1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("Updated Name"));
    }

    @Test
    @DisplayName("DELETE /api/v1/devices/{ip} - 删除设备成功")
    void deleteDevice_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/devices/192.168.1.1"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/v1/devices/batch - 批量删除成功")
    void deleteDevices_Success() throws Exception {
        BatchResultDTO result = new BatchResultDTO(1, 0, Collections.emptyList());
        when(deviceService.deleteDevices(anyList())).thenReturn(result);

        mockMvc.perform(delete("/api/v1/devices/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of("192.168.1.1"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.successCount").value(1));
    }
}