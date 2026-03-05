package com.smartkit.toolbox.controller;

import com.alibaba.excel.EasyExcel;
import com.smartkit.toolbox.annotation.LogOperation;
import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;
import com.smartkit.toolbox.model.Result;
import com.smartkit.toolbox.model.dto.BatchResultDTO;
import com.smartkit.toolbox.model.dto.DeviceCreateDTO;
import com.smartkit.toolbox.model.dto.DeviceQueryDTO;
import com.smartkit.toolbox.model.dto.DeviceUpdateDTO;
import com.smartkit.toolbox.service.DeviceExcelService;
import com.smartkit.toolbox.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 设备管理控制器，提供设备相关的REST API接口。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/devices")
@Tag(name = "Device Management", description = "设备管理API")
@Validated
public class DeviceController {

    /**
     * 设备服务
     */
    private final DeviceService deviceService;

    /**
     * 设备Excel服务
     */
    private final DeviceExcelService deviceExcelService;

    /**
     * 构造方法，注入依赖的服务
     *
 * @param deviceService 设备服务
     * @param deviceExcelService 设备Excel服务
     */
    public DeviceController(DeviceService deviceService, DeviceExcelService deviceExcelService) {
        this.deviceService = deviceService;
        this.deviceExcelService = deviceExcelService;
    }

    /**
     * 添加单个设备
     *
     * @param dto 设备创建DTO
     * @return 创建的设备信息
     */
    @PostMapping
    @Operation(summary = "添加单个设备")
    @LogOperation(
        operationType = OperationType.CREATE,
        objectId = "#dto.ip",
        objectName = "#dto.name",
        description = "'创建设备: ' + #dto.name",
        operatorIp = true
    )
    public Result<Device> addDevice(@Valid @RequestBody DeviceCreateDTO dto) {
        Device device = deviceService.addDevice(dto);
        return Result.success(device);
    }

    /**
     * 批量添加设备
     *
     * @param devices 设备创建DTO列表，最多100个
     * @return 批量操作结果
     */
    @PostMapping("/batch")
    @Operation(summary = "批量添加设备")
    @LogOperation(
        operationType = OperationType.IMPORT,
        objectId = "'batch'",
        objectName = "'批量设备'",
        description = "'批量导入 ' + #devices.size() + ' 台设备'",
        operatorIp = true
    )
    public Result<BatchResultDTO> addDevices(
            @Valid @Size(max = 100, message = "{error.device.batch.add.exceeded}")
            @RequestBody List<DeviceCreateDTO> devices) {
        BatchResultDTO result = deviceService.addDevices(devices);
        return Result.success(result);
    }

    /**
     * 查询单个设备
     *
     * @param ip 设备IP地址
     * @return 设备信息
     */
    @GetMapping("/{ip}")
    @Operation(summary = "查询单个设备")
    public Result<Device> getDevice(@PathVariable @Pattern(regexp = "^(?:(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$", message = "{error.device.ip.invalid}") String ip) {
        Device device = deviceService.getDevice(ip);
        return Result.success(device);
    }

    /**
     * 批量查询设备列表
     *
     * @param query 查询条件DTO
     * @return 设备列表及总数
     */
    @GetMapping
    @Operation(summary = "批量查询设备列表")
    public Result<DeviceListResult> getDevices(@Valid DeviceQueryDTO query) {
        List<Device> devices = deviceService.getDevices(query);
        long total = deviceService.count(query);
        return Result.success(new DeviceListResult(devices, total));
    }

    /**
     * 更新设备信息
     *
     * @param ip 设备IP地址
     * @param dto 设备更新DTO
     * @return 更新后的设备信息
     */
    @PutMapping("/{ip}")
    @Operation(summary = "更新设备信息")
    @LogOperation(
        operationType = OperationType.UPDATE,
        objectId = "#ip",
        objectName = "#dto.name",
        description = "'更新设备: ' + #ip",
        operatorIp = true
    )
    public Result<Device> updateDevice(@PathVariable @Pattern(regexp = "^(?:(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$", message = "{error.device.ip.invalid}") String ip, @Valid @RequestBody DeviceUpdateDTO dto) {
        Device device = deviceService.updateDevice(ip, dto);
        return Result.success(device);
    }

    /**
     * 删除单个设备
     *
     * @param ip 设备IP地址
     * @return 操作结果
     */
    @DeleteMapping("/{ip}")
    @Operation(summary = "删除单个设备")
    @LogOperation(
        operationType = OperationType.DELETE,
        objectId = "#ip",
        description = "'删除设备: ' + #ip",
        operatorIp = true
    )
    public Result<Void> deleteDevice(@PathVariable @Pattern(regexp = "^(?:(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$", message = "{error.device.ip.invalid}") String ip) {
        deviceService.deleteDevice(ip);
        return Result.success();
    }

    /**
     * 批量删除设备
     *
     * @param ips 设备IP地址列表，最多100个
     * @return 批量操作结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除设备")
    @LogOperation(
        operationType = OperationType.DELETE,
        objectId = "'batch'",
        objectName = "'批量设备'",
        description = "'批量删除 ' + #ips.size() + ' 台设备'",
        operatorIp = true
    )
    public Result<BatchResultDTO> deleteDevices(
            @Valid @Size(max = 100, message = "{error.device.batch.delete.exceeded}")
            @RequestBody List<String> ips) {
        BatchResultDTO result = deviceService.deleteDevices(ips);
        return Result.success(result);
    }

    /**
     * 导出设备到Excel
     *
     * @param query 查询条件
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
    @GetMapping("/export")
    @Operation(summary = "导出设备到Excel")
    @LogOperation(
        operationType = OperationType.EXPORT,
        objectId = "'excel'",
        objectName = "'Excel导出'",
        description = "'导出设备到Excel'",
        operatorIp = true
    )
    public void exportDevices(@Valid DeviceQueryDTO query, HttpServletResponse response) throws IOException {
        List<Device> devices = deviceService.getDevices(query);
        deviceExcelService.exportDevices(devices, response);
    }

    /**
     * 下载导入模板
     *
     * @param response HTTP响应对象
     * @throws IOException IO异常
     */
    @GetMapping("/template")
    @Operation(summary = "下载导入模板")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        deviceExcelService.downloadTemplate(response);
    }

    /**
     * 从Excel导入设备
     *
     * @param file 上传的Excel文件
     * @return 批量操作结果
     * @throws IOException IO异常
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "从Excel导入设备")
    @LogOperation(
        operationType = OperationType.IMPORT,
        objectId = "'excel'",
        objectName = "'Excel导入'",
        description = "'从Excel导入设备'",
        operatorIp = true
    )
    public Result<BatchResultDTO> importDevices(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("{error.file.required}");
        }
        List<DeviceExcelService.DeviceExcelDTO> excelData = EasyExcel.read(file.getInputStream())
            .head(DeviceExcelService.DeviceExcelDTO.class)
            .sheet()
            .doReadSync();

        BatchResultDTO result = deviceExcelService.importDevices(excelData);
        return Result.success(result);
    }

    /**
     * 设备列表返回结果内部类
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class DeviceListResult {
        /**
         * 设备列表
         */
        private List<Device> list;

        /**
         * 总记录数
         */
        private long total;
    }
}