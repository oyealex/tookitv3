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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/devices")
@Tag(name = "Device Management", description = "设备管理API")
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceExcelService deviceExcelService;

    public DeviceController(DeviceService deviceService, DeviceExcelService deviceExcelService) {
        this.deviceService = deviceService;
        this.deviceExcelService = deviceExcelService;
    }

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

    @GetMapping("/{ip}")
    @Operation(summary = "查询单个设备")
    public Result<Device> getDevice(@PathVariable String ip) {
        Device device = deviceService.getDevice(ip);
        return Result.success(device);
    }

    @GetMapping
    @Operation(summary = "批量查询设备列表")
    public Result<DeviceListResult> getDevices(DeviceQueryDTO query) {
        List<Device> devices = deviceService.getDevices(query);
        long total = deviceService.count(query);
        return Result.success(new DeviceListResult(devices, total));
    }

    @PutMapping("/{ip}")
    @Operation(summary = "更新设备信息")
    @LogOperation(
        operationType = OperationType.UPDATE,
        objectId = "#ip",
        objectName = "#dto.name",
        description = "'更新设备: ' + #ip",
        operatorIp = true
    )
    public Result<Device> updateDevice(@PathVariable String ip, @Valid @RequestBody DeviceUpdateDTO dto) {
        Device device = deviceService.updateDevice(ip, dto);
        return Result.success(device);
    }

    @DeleteMapping("/{ip}")
    @Operation(summary = "删除单个设备")
    @LogOperation(
        operationType = OperationType.DELETE,
        objectId = "#ip",
        description = "'删除设备: ' + #ip",
        operatorIp = true
    )
    public Result<Void> deleteDevice(@PathVariable String ip) {
        deviceService.deleteDevice(ip);
        return Result.success();
    }

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

    @GetMapping("/export")
    @Operation(summary = "导出设备到Excel")
    @LogOperation(
        operationType = OperationType.EXPORT,
        objectId = "'excel'",
        objectName = "'Excel导出'",
        description = "'导出设备到Excel'",
        operatorIp = true
    )
    public void exportDevices(DeviceQueryDTO query, HttpServletResponse response) throws IOException {
        List<Device> devices = deviceService.getDevices(query);
        deviceExcelService.exportDevices(devices, response);
    }

    @GetMapping("/template")
    @Operation(summary = "下载导入模板")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        deviceExcelService.downloadTemplate(response);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "从Excel导入设备")
    @LogOperation(
        operationType = OperationType.IMPORT,
        objectId = "'excel'",
        objectName = "'Excel导入'",
        description = "'从Excel导入设备'",
        operatorIp = true
    )
    public Result<BatchResultDTO> importDevices(@RequestParam("file") MultipartFile file) throws IOException {
        List<DeviceExcelService.DeviceExcelDTO> excelData = EasyExcel.read(file.getInputStream())
            .head(DeviceExcelService.DeviceExcelDTO.class)
            .sheet()
            .doReadSync();
        
        BatchResultDTO result = deviceExcelService.importDevices(excelData);
        return Result.success(result);
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class DeviceListResult {
        private List<Device> list;
        private long total;
    }
}