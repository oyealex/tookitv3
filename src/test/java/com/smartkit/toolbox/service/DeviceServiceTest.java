package com.smartkit.toolbox.service;

import com.smartkit.toolbox.common.BusinessException;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.DeviceType;
import com.smartkit.toolbox.model.dto.BatchResultDTO;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.model.dto.DeviceQueryDTO;
import com.smartkit.toolbox.model.dto.DeviceUpdateDTO;
import com.smartkit.toolbox.repository.DeviceRepository;
import com.smartkit.toolbox.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private MessageSource messageSource;

    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenAnswer(inv -> inv.getArgument(0));
        deviceService = new DeviceServiceImpl(deviceRepository, messageSource);
    }

    private DeviceCreateDTO createValidDTO() {
        DeviceCreateDTO dto = new DeviceCreateDTO();
        dto.setIp("192.168.1.1");
        dto.setName("Test Device");
        dto.setType(DeviceType.STORAGE);
        dto.setModel("Model-X");
        dto.setVersion("v1.0");
        dto.setUsername("admin");
        dto.setPassword("password");
        return dto;
    }

    @Test
    @DisplayName("添加设备 - 成功")
    void addDevice_Success() {
        DeviceCreateDTO dto = createValidDTO();
        when(deviceRepository.existsByIp(anyString())).thenReturn(false);
        when(deviceRepository.count()).thenReturn(0L);
        doNothing().when(deviceRepository).insert(any());
        when(deviceRepository.findByIp(anyString())).thenReturn(Optional.of(new Device()));

        Device result = deviceService.addDevice(dto);

        assertNotNull(result);
        verify(deviceRepository).insert(any());
    }

    @Test
    @DisplayName("添加设备 - IP无效")
    void addDevice_InvalidIp() {
        DeviceCreateDTO dto = createValidDTO();
        dto.setIp("invalid-ip");

        assertThrows(BusinessException.class, () -> deviceService.addDevice(dto));
    }

    @Test
    @DisplayName("添加设备 - IP重复")
    void addDevice_DuplicateIp() {
        DeviceCreateDTO dto = createValidDTO();
        when(deviceRepository.existsByIp(anyString())).thenReturn(true);

        assertThrows(BusinessException.class, () -> deviceService.addDevice(dto));
    }

    @Test
    @DisplayName("添加设备 - 名称默认值")
    void addDevice_DefaultName() {
        DeviceCreateDTO dto = createValidDTO();
        dto.setName(null);
        when(deviceRepository.existsByIp(anyString())).thenReturn(false);
        when(deviceRepository.count()).thenReturn(0L);
        doAnswer(invocation -> {
            Device device = invocation.getArgument(0);
            assertEquals("STORAGE-192.168.1.1", device.getName());
            return null;
        }).when(deviceRepository).insert(any());
        when(deviceRepository.findByIp(anyString())).thenReturn(Optional.of(new Device()));

        deviceService.addDevice(dto);
    }

    @Test
    @DisplayName("批量添加设备 - 成功")
    void addDevices_Success() {
        List<DeviceCreateDTO> devices = List.of(createValidDTO());
        when(deviceRepository.existsByIp(anyString())).thenReturn(false);
        when(deviceRepository.count()).thenReturn(0L);
        doNothing().when(deviceRepository).insert(any());

        BatchResultDTO result = deviceService.addDevices(devices);

        assertEquals(1, result.getSuccessCount());
        assertEquals(0, result.getFailCount());
    }

    @Test
    @DisplayName("批量添加设备 - 超过限制")
    void addDevices_ExceedLimit() {
        List<DeviceCreateDTO> devices = new java.util.ArrayList<>();
        for (int i = 0; i < 101; i++) {
            devices.add(createValidDTO());
        }

        assertThrows(BusinessException.class, () -> deviceService.addDevices(devices));
    }

    @Test
    @DisplayName("查询单个设备 - 成功")
    void getDevice_Success() {
        Device device = new Device();
        device.setIp("192.168.1.1");
        when(deviceRepository.findByIp("192.168.1.1")).thenReturn(Optional.of(device));

        Device result = deviceService.getDevice("192.168.1.1");

        assertEquals("192.168.1.1", result.getIp());
    }

    @Test
    @DisplayName("查询单个设备 - 不存在")
    void getDevice_NotFound() {
        when(deviceRepository.findByIp(anyString())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> deviceService.getDevice("192.168.1.1"));
    }

    @Test
    @DisplayName("批量查询设备 - 成功")
    void getDevices_Success() {
        DeviceQueryDTO query = new DeviceQueryDTO();
        query.setOffset(0);
        query.setLimit(10);
        when(deviceRepository.findAll(anyInt(), anyInt(), any(), any(), any(), any(), any()))
            .thenReturn(List.of(new Device()));

        List<Device> result = deviceService.getDevices(query);

        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("批量查询设备 - limit超过最大值")
    void getDevices_ExceedLimit() {
        DeviceQueryDTO query = new DeviceQueryDTO();
        query.setLimit(101);

        assertThrows(BusinessException.class, () -> deviceService.getDevices(query));
    }

    @Test
    @DisplayName("更新设备 - 成功")
    void updateDevice_Success() {
        Device existing = new Device();
        existing.setIp("192.168.1.1");
        existing.setType(DeviceType.STORAGE);

        DeviceUpdateDTO dto = new DeviceUpdateDTO();
        dto.setName("Updated Name");

        when(deviceRepository.findByIp(anyString())).thenReturn(Optional.of(existing));
        doNothing().when(deviceRepository).update(any());

        Device result = deviceService.updateDevice("192.168.1.1", dto);

        assertNotNull(result);
        verify(deviceRepository).update(any());
    }

    @Test
    @DisplayName("删除设备 - 成功")
    void deleteDevice_Success() {
        when(deviceRepository.existsByIp(anyString())).thenReturn(true);
        doNothing().when(deviceRepository).deleteByIp(anyString());

        deviceService.deleteDevice("192.168.1.1");

        verify(deviceRepository).deleteByIp("192.168.1.1");
    }

    @Test
    @DisplayName("删除设备 - 不存在")
    void deleteDevice_NotFound() {
        when(deviceRepository.existsByIp(anyString())).thenReturn(false);

        assertThrows(BusinessException.class, () -> deviceService.deleteDevice("192.168.1.1"));
    }

    @Test
    @DisplayName("批量删除设备 - 成功")
    void deleteDevices_Success() {
        when(deviceRepository.existsByIp(anyString())).thenReturn(true);
        doNothing().when(deviceRepository).deleteByIp(anyString());

        BatchResultDTO result = deviceService.deleteDevices(List.of("192.168.1.1"));

        assertEquals(1, result.getSuccessCount());
    }
}