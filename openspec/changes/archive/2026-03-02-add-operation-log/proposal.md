## Why

当前系统缺乏操作追溯能力，无法追踪用户对设备的具体操作行为。当出现问题时，运维人员难以定位是哪些操作导致了故障。同时，审计合规要求也需要记录关键业务操作的历史。增加操作日志模块可以满足问题追溯和审计合规的需求。

## What Changes

- 新增 `OperationLog` 实体类，包含操作时间、操作对象简略信息、操作结果、操作失败原因等字段
- 新增 `OperationLogRepository` 提供数据库持久化支持
- 新增 `OperationLogService` 提供业务逻辑封装
- 新增 `OperationLogController` 提供 REST API 接口
- 新增 `OperationLogQueryDTO` 支持分页和条件查询
- 提供便捷的操作日志记录接口，供其他模块调用
- 操作日志仅支持新增和查询，不支持删除

## Capabilities

### New Capabilities
- `operation-log`: 操作日志模块，提供操作记录和查询功能

### Modified Capabilities
- （无）

## Impact

- 新增数据库表 `operation_log`
- 新增 REST API 端点 `/api/v1/operation-logs`
- 需要在设备管理模块的增删改操作中集成日志记录调用
- 项目包结构新增 `model/OperationLog.java`、`repository/OperationLogRepository.java`、`service/OperationLogService.java`、`controller/OperationLogController.java`、`model/dto/OperationLogQueryDTO.java`