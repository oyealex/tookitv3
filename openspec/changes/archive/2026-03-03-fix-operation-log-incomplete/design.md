# Design: fix-operation-log-incomplete

## Context

当前操作日志功能中，通过 `@LogOperation` 注解或 `OperationLogHelper` 静态方法创建 `OperationLog` 对象时，没有主动设置时间字段（`operationTime` 和 `createdAt`）。虽然 Service 层和 Repository 层会补充设置，但这种分散的设置方式不符合最佳实践，可能导致时间记录不完整或难以追踪。

## Goals / Non-Goals

**Goals:**
- 在 `OperationLogHelper` 的 `log()` 和 `logAsync()` 方法中主动设置 `operationTime`
- 在 `OperationLogAspect` 的 `extractLogInfo()` 方法中主动设置 `operationTime`
- 为上述修改添加对应的单元测试（UT），验证时间字段正确设置

**Non-Goals:**
- 不修改数据库表结构
- 不修改 API 接口
- 不涉及性能优化

## Decisions

### Decision 1: 时间设置位置

**选择**：在 `OperationLogHelper` 和 `OperationLogAspect` 创建 OperationLog 对象后立即设置时间

**理由**：
- 确保时间在记录时就被设置，符合"数据在源头完整"的原则
- 便于调试和追踪，明确时间是在哪个环节设置的
- 不影响现有的 Service 层和 Repository 层逻辑（它们已经有容错处理）

### Decision 2: 测试策略

**选择**：为 `OperationLogHelper` 和 `OperationLogAspect` 添加单元测试

**理由**：
- `OperationLogHelper`: 静态工具类，适合单元测试，验证其方法是否正确设置时间
- `OperationLogAspect`: AOP 切面，可以使用 Spring 的 AOP 测试支持或 mock 来验证

## Implementation Plan

### 1. 修改 OperationLogHelper
```java
// 在 log() 和 logAsync() 方法的 OperationLog 对象创建后添加：
log.setOperationTime(LocalDateTime.now());
```

### 2. 修改 OperationLogAspect
```java
// 在 extractLogInfo() 方法的 OperationLog 对象创建后添加：
log.setOperationTime(java.time.LocalDateTime.now());
```

### 3. 添加单元测试
- `OperationLogHelperTest`: 测试 Helper 的 log/logAsync 方法是否正确设置 operationTime
- `OperationLogAspectTest`: 测试 Aspect 在截获方法时是否正确设置 operationTime

## Risks / Trade-offs

** Risks:**
- 无明显风险，这是一个简单的修复

** Trade-offs:**
- 时间精度：当前使用 `LocalDateTime.now()`，精确到纳秒级别，符合需求