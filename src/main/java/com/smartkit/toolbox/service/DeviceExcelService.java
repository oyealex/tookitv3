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

/**
 * 设备Excel服务类，提供设备的导入导出功能。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@Service
public class DeviceExcelService {

    /**
     * 最大失败原因记录数
     */
    private static final int MAX_FAIL_REASONS = 10;

    /**
     * 最大设备数量限制
     */
    private static final int MAX_DEVICES = 1000;

    /**
     * 日期格式化器
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 设备仓库
     */
    private final DeviceRepository deviceRepository;

    /**
     * 构造方法，注入依赖
     *
     * @param deviceRepository 设备仓库
     */
    public DeviceExcelService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * 导出设备列表到Excel文件
     *
     * @param devices 设备列表
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
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

    /**
     * 下载设备导入模板
     *
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
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

    /**
     * 从Excel导入设备
     *
     * @param excelData Excel数据列表
     * @return 批量操作结果
     */
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

    /**
     * 验证Excel行数据
     *
     * @param dto Excel数据DTO
     * @param rowNum 行号
     */
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

    /**
     * 解析设备名称
     *
     * @param name 设备名称
     * @param type 设备类型
     * @param ip 设备IP
     * @return 设备名称
     */
    private String resolveName(String name, String type, String ip) {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return type.toUpperCase() + "-" + ip;
    }

    /**
     * 将设备对象转换为Excel DTO
     *
     * @param device 设备对象
     * @return Excel DTO
     */
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

    /**
     * 设备Excel数据传输对象，用于Excel导入导出。
     */
    @lombok.Data
    public static class DeviceExcelDTO {
        /**
         * IP地址
         */
        @com.alibaba.excel.annotation.ExcelProperty("IP地址")
        private String ip;

        /**
         * 设备名称
         */
        @com.alibaba.excel.annotation.ExcelProperty("设备名称")
        private String name;

        /**
         * 设备类型
         */
        @com.alibaba.excel.annotation.ExcelProperty("设备类型")
        private String type;

        /**
         * 型号
         */
        @com.alibaba.excel.annotation.ExcelProperty("型号")
        private String model;

        /**
         * 版本
         */
        @com.alibaba.excel.annotation.ExcelProperty("版本")
        private String version;

        /**
         * 登录用户名
         */
        @com.alibaba.excel.annotation.ExcelProperty("登录用户名")
        private String username;

        /**
         * 创建时间
         */
        @com.alibaba.excel.annotation.ExcelProperty("创建时间")
        private String createdAt;

        /**
         * 更新时间
         */
        @com.alibaba.excel.annotation.ExcelProperty("更新时间")
        private String updatedAt;
    }
}