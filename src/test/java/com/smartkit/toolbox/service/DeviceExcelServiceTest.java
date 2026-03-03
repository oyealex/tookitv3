package com.smartkit.toolbox.service;

import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.DeviceType;
import com.smartkit.toolbox.model.dto.BatchResultDTO;
import com.smartkit.toolbox.repository.DeviceRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceExcelServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private HttpServletResponse response;

    private DeviceExcelService deviceExcelService;

    @BeforeEach
    void setUp() {
        deviceExcelService = new DeviceExcelService(deviceRepository);
    }

    @Test
    @DisplayName("导入设备 - 成功")
    void importDevices_Success() {
        List<DeviceExcelService.DeviceExcelDTO> excelData = new java.util.ArrayList<>();
        DeviceExcelService.DeviceExcelDTO dto = new DeviceExcelService.DeviceExcelDTO();
        dto.setIp("192.168.1.1");
        dto.setType("STORAGE");
        excelData.add(dto);

        when(deviceRepository.existsByIp(anyString())).thenReturn(false);
        when(deviceRepository.count()).thenReturn(0L);
        doNothing().when(deviceRepository).insert(any());

        BatchResultDTO result = deviceExcelService.importDevices(excelData);

        assertEquals(1, result.getSuccessCount());
        assertEquals(0, result.getFailCount());
    }

    @Test
    @DisplayName("导入设备 - IP重复")
    void importDevices_DuplicateIp() {
        List<DeviceExcelService.DeviceExcelDTO> excelData = new java.util.ArrayList<>();
        DeviceExcelService.DeviceExcelDTO dto = new DeviceExcelService.DeviceExcelDTO();
        dto.setIp("192.168.1.1");
        dto.setType("STORAGE");
        excelData.add(dto);

        when(deviceRepository.existsByIp(anyString())).thenReturn(true);
        when(deviceRepository.count()).thenReturn(0L);

        BatchResultDTO result = deviceExcelService.importDevices(excelData);

        assertEquals(0, result.getSuccessCount());
        assertEquals(1, result.getFailCount());
        assertTrue(result.getFailReasons().get(0).getReason().contains("IP地址已存在"));
    }

    @Test
    @DisplayName("导入设备 - 无效IP格式")
    void importDevices_InvalidIp() {
        List<DeviceExcelService.DeviceExcelDTO> excelData = new java.util.ArrayList<>();
        DeviceExcelService.DeviceExcelDTO dto = new DeviceExcelService.DeviceExcelDTO();
        dto.setIp("invalid-ip");
        dto.setType("STORAGE");
        excelData.add(dto);

        BatchResultDTO result = deviceExcelService.importDevices(excelData);

        assertEquals(0, result.getSuccessCount());
        assertTrue(result.getFailReasons().get(0).getReason().contains("IP地址格式错误"));
    }

    @Test
    @DisplayName("导入设备 - 无效设备类型")
    void importDevices_InvalidType() {
        List<DeviceExcelService.DeviceExcelDTO> excelData = new java.util.ArrayList<>();
        DeviceExcelService.DeviceExcelDTO dto = new DeviceExcelService.DeviceExcelDTO();
        dto.setIp("192.168.1.1");
        dto.setType("INVALID_TYPE");
        excelData.add(dto);

        when(deviceRepository.existsByIp(anyString())).thenReturn(false);

        BatchResultDTO result = deviceExcelService.importDevices(excelData);

        assertEquals(0, result.getSuccessCount());
        assertTrue(result.getFailReasons().get(0).getReason().contains("设备类型无效"));
    }

    @Test
    @DisplayName("导入设备 - 缺少必填字段")
    void importDevices_MissingRequiredFields() {
        List<DeviceExcelService.DeviceExcelDTO> excelData = new java.util.ArrayList<>();
        DeviceExcelService.DeviceExcelDTO dto = new DeviceExcelService.DeviceExcelDTO();
        dto.setIp(null);
        dto.setType("STORAGE");
        excelData.add(dto);

        BatchResultDTO result = deviceExcelService.importDevices(excelData);

        assertEquals(0, result.getSuccessCount());
        assertTrue(result.getFailReasons().get(0).getReason().contains("IP地址不能为空"));
    }
}