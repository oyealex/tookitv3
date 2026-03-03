# Proposal: fix-operation-log-incomplete

## Why

当前操作日志记录功能存在缺陷：在通过 `@LogOperation` 注解或 `OperationLogHelper` 静态方法记录日志时，创建的 `OperationLog` 对象没有主动设置时间字段（`operationTime` 和 `createdAt`）。虽然 Service 层和 Repository 层会补充设置时间，但这种分散的设置方式容易导致时间记录不完整或不一致的问题。系统应确保每次记录操作日志时都完整地包含时间信息。

## What Changes

1. **修改 OperationLogHelper**：在 `log()` 和 `logAsync()` 方法中，创建 OperationLog 对象后立即设置 `operationTime = LocalDateTime.now()`
2. **修改 OperationLogAspect**：在 `extractLogInfo()` 方法中，创建 OperationLog 对象后立即设置 `operationTime = LocalDateTime.now()`
3. **统一时间设置逻辑**：确保所有记录日志的入口点都主动设置时间，避免依赖后端层补充设置

## Capabilities

### Modified Capabilities
- `operation-log`: 操作日志记录功能需要确保在记录时主动设置时间字段，而不是依赖后端层补充

## Impact

- 修改文件：
  - `src/main/java/com/smartkit/toolbox/service/OperationLogHelper.java`
  - `src/main/java/com/smartkit/toolbox/aspect/OperationLogAspect.java`