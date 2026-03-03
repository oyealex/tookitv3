package com.smartkit.toolbox.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.DeviceType;
import com.smartkit.toolbox.model.dto.BatchResultDTO;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.repository.DeviceRepository;
import com.smartkit.toolbox.util.IpValidator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceExcelService {

    private static final int MAX_FAIL_REASONS = 10;
    private static final int MAX_DEVICES = 1000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DeviceRepository deviceRepository;

    public DeviceExcelService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public void exportDevices(List<Device> devices, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("devices", StandardCharsets.UTF_8).replace("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<DeviceExcelDTO> excelData = devices.stream()
            .map(this::toExcelDTO)
            .toList();

        EasyExcel.write(response.getOutputStream(), DeviceExcelDTO.class)
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .sheet("设备列表")
            .doWrite(excelData);
    }

    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("device_template", StandardCharsets.UTF_8).replace("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<DeviceExcelDTO> templateData = new ArrayList<>();
        
        DeviceExcelDTO description = new DeviceExcelDTO();
        description.setIp("必填，IPv4格式");
        description.setName("选填，最长120字符");
        description.setType("必填，STORAGE/SERVER/NETWORK");
        description.setModel("选填");
        description.setVersion("选填");
        description.setUsername("选填");
        templateData.add(description);

        DeviceExcelDTO example = new DeviceExcelDTO();
        example.setIp("192.168.1.1");
        example.setName("示例设备");
        example.setType("STORAGE");
        example.setModel("Model-X");
        example.setVersion("v1.0");
        example.setUsername("admin");
        templateData.add(example);

        EasyExcel.write(response.getOutputStream(), DeviceExcelDTO.class)
            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
            .sheet("设备导入模板")
            .doWrite(templateData);
    }

    public BatchResultDTO importDevices(List<DeviceExcelDTO> excelData) {
        int successCount = 0;
        List<BatchResultDTO.FailReason> failReasons = new ArrayList<>();
        long currentCount = deviceRepository.count();

        for (int i = 0; i < excelData.size(); i++) {
            DeviceExcelDTO dto = excelData.get(i);
            int rowNum = i + 2;

            try {
                if (currentCount + successCount >= MAX_DEVICES) {
                    if (failReasons.size() < MAX_FAIL_REASONS) {
                        failReasons.add(new BatchResultDTO.FailReason(rowNum, dto.getIp(), "设备总数已达上限"));
                    }
                    continue;
                }

                validateExcelRow(dto, rowNum);

                Device device = new Device();
                device.setIp(dto.getIp());
                device.setName(resolveName(dto.getName(), dto.getType(), dto.getIp()));
                device.setType(DeviceType.valueOf(dto.getType().toUpperCase()));
                device.setModel(dto.getModel());
                device.setVersion(dto.getVersion());
                device.setUsername(dto.getUsername());
                device.setPassword("default");

                deviceRepository.insert(device);
                successCount++;
            } catch (Exception e) {
                if (failReasons.size() < MAX_FAIL_REASONS) {
                    failReasons.add(new BatchResultDTO.FailReason(rowNum, dto.getIp(), e.getMessage()));
                }
            }
        }

        return new BatchResultDTO(successCount, excelData.size() - successCount, failReasons);
    }

    private void validateExcelRow(DeviceExcelDTO dto, int rowNum) {
        if (dto.getIp() == null || dto.getIp().isEmpty()) {
            throw new IllegalArgumentException("IP地址不能为空");
        }
        if (!IpValidator.isValid(dto.getIp())) {
            throw new IllegalArgumentException("IP地址格式错误");
        }
        if (deviceRepository.existsByIp(dto.getIp())) {
            throw new IllegalArgumentException("IP地址已存在");
        }
        if (dto.getType() == null || dto.getType().isEmpty()) {
            throw new IllegalArgumentException("设备类型不能为空");
        }
        try {
            DeviceType.valueOf(dto.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("设备类型无效，有效值: STORAGE/SERVER/NETWORK");
        }
    }

    private String resolveName(String name, String type, String ip) {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return type.toUpperCase() + "-" + ip;
    }

    private DeviceExcelDTO toExcelDTO(Device device) {
        DeviceExcelDTO dto = new DeviceExcelDTO();
        dto.setIp(device.getIp());
        dto.setName(device.getName());
        dto.setType(device.getType().name());
        dto.setModel(device.getModel());
        dto.setVersion(device.getVersion());
        dto.setUsername(device.getUsername());
        if (device.getCreatedAt() != null) {
            dto.setCreatedAt(device.getCreatedAt().format(DATE_FORMATTER));
        }
        if (device.getUpdatedAt() != null) {
            dto.setUpdatedAt(device.getUpdatedAt().format(DATE_FORMATTER));
        }
        return dto;
    }

    @lombok.Data
    public static class DeviceExcelDTO {
        @com.alibaba.excel.annotation.ExcelProperty("IP地址")
        private String ip;

        @com.alibaba.excel.annotation.ExcelProperty("设备名称")
        private String name;

        @com.alibaba.excel.annotation.ExcelProperty("设备类型")
        private String type;

        @com.alibaba.excel.annotation.ExcelProperty("型号")
        private String model;

        @com.alibaba.excel.annotation.ExcelProperty("版本")
        private String version;

        @com.alibaba.excel.annotation.ExcelProperty("登录用户名")
        private String username;

        @com.alibaba.excel.annotation.ExcelProperty("创建时间")
        private String createdAt;

        @com.alibaba.excel.annotation.ExcelProperty("更新时间")
        private String updatedAt;
    }
}