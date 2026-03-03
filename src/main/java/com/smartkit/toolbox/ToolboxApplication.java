package com.smartkit.toolbox;

import com.smartkit.toolbox.model.Device;
import com.smartkit.toolbox.model.DeviceType;
import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;
import com.smartkit.toolbox.repository.DeviceRepository;
import com.smartkit.toolbox.repository.OperationLogRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class ToolboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolboxApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(DeviceRepository deviceRepository, OperationLogRepository operationLogRepository) {
        return args -> {
            // 检查是否已有数据
            if (deviceRepository.count() > 0) {
                System.out.println("数据库已有数据，跳过初始化");
                return;
            }

            System.out.println("初始化测试数据...");

            // 创建设备测试数据
            List<Device> devices = List.of(
                createDevice("192.168.1.10", "测试服务器1", DeviceType.SERVER, "admin", "pass123"),
                createDevice("192.168.1.11", "测试服务器2", DeviceType.SERVER, "admin", "pass123"),
                createDevice("192.168.1.20", "存储设备1", DeviceType.STORAGE, "admin", "pass123"),
                createDevice("192.168.1.30", "网络设备1", DeviceType.NETWORK, "admin", "pass123"),
                createDevice("192.168.1.31", "网络设备2", DeviceType.NETWORK, "admin", "pass123")
            );

            deviceRepository.batchInsert(devices);
            System.out.println("已创建 " + devices.size() + " 条设备数据");

            // 创建操作日志测试数据
            LocalDateTime now = LocalDateTime.now();
            List<OperationLog> logs = List.of(
                createLog(OperationType.CREATE, "DEVICE", "192.168.1.10", "测试服务器1", "创建设备: 测试服务器1", OperationResult.SUCCESS, now.minusHours(2)),
                createLog(OperationType.CREATE, "DEVICE", "192.168.1.11", "测试服务器2", "创建设备: 测试服务器2", OperationResult.SUCCESS, now.minusHours(1).minusMinutes(30)),
                createLog(OperationType.CREATE, "DEVICE", "192.168.1.20", "存储设备1", "创建设备: 存储设备1", OperationResult.SUCCESS, now.minusHours(1)),
                createLog(OperationType.CREATE, "DEVICE", "192.168.1.30", "网络设备1", "创建设备: 网络设备1", OperationResult.SUCCESS, now.minusMinutes(30)),
                createLog(OperationType.UPDATE, "DEVICE", "192.168.1.10", "测试服务器1", "更新设备: 192.168.1.10", OperationResult.SUCCESS, now.minusMinutes(20)),
                createLog(OperationType.CREATE, "DEVICE", "192.168.1.31", "网络设备2", "创建设备: 网络设备2", OperationResult.SUCCESS, now.minusMinutes(10)),
                createLog(OperationType.DELETE, "DEVICE", "192.168.1.99", "已删除设备", "删除设备: 192.168.1.99", OperationResult.SUCCESS, now.minusMinutes(5)),
                createLog(OperationType.CREATE, "DEVICE", "192.168.1.99", "测试失败", "创建设备: 测试失败", OperationResult.FAILURE, now.minusMinutes(1), "IP地址已存在")
            );

            operationLogRepository.batchInsert(logs);
            System.out.println("已创建 " + logs.size() + " 条操作日志数据");
            System.out.println("测试数据初始化完成！");
        };
    }

    private Device createDevice(String ip, String name, DeviceType type, String username, String password) {
        Device device = new Device();
        device.setIp(ip);
        device.setName(name);
        device.setType(type);
        device.setUsername(username);
        device.setPassword(password);
        return device;
    }

    private OperationLog createLog(OperationType operationType, String objectType, String objectId,
                                    String objectName, String description, OperationResult result,
                                    LocalDateTime operationTime) {
        return createLog(operationType, objectType, objectId, objectName, description, result, operationTime, null);
    }

    private OperationLog createLog(OperationType operationType, String objectType, String objectId,
                                    String objectName, String description, OperationResult result,
                                    LocalDateTime operationTime, String failureReason) {
        OperationLog log = new OperationLog();
        log.setOperationType(operationType);
        log.setObjectType(objectType);
        log.setObjectId(objectId);
        log.setObjectName(objectName);
        log.setDescription(description);
        log.setResult(result);
        log.setOperationTime(operationTime);
        log.setFailureReason(failureReason);
        log.setOperatorIp("192.168.1.100");
        return log;
    }
}