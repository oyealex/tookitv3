# Delta Spec: operation-log (fix-operation-log-incomplete)

## ADDED Requirements

### Requirement: 操作日志记录时主动设置时间
系统 SHALL 在记录操作日志时主动设置 `operationTime` 字段，确保时间信息完整。

#### Scenario: Helper 同步记录时设置时间
- **WHEN** 调用 `OperationLogHelper.log()` 方法记录操作日志
- **THEN** OperationLog 对象的 `operationTime` 字段被设置为当前时间

#### Scenario: Helper 异步记录时设置时间
- **WHEN** 调用 `OperationLogHelper.logAsync()` 方法记录操作日志
- **THEN** OperationLog 对象的 `operationTime` 字段被设置为当前时间

#### Scenario: AOP 切面记录时设置时间
- **WHEN** 使用 `@LogOperation` 注解记录操作日志
- **THEN** OperationLog 对象的 `operationTime` 字段被设置为当前时间