## Context

当前系统是一个基于 Spring Boot 的设备管理 REST API 应用，使用 SQLite 作为数据库。项目已实现设备管理功能，包括设备的增删改查、批量操作和 Excel 导入导出。

现有代码采用分层架构：
- Controller 层处理 REST API 请求
- Service 层处理业务逻辑
- Repository 层处理数据库访问
- Model 层定义数据模型和 DTO

现有分页查询风格：
- 查询参数使用 DTO 接收（如 `DeviceQueryDTO`），包含 `offset` 和 `limit` 字段
- 返回结果使用自定义包装类（如 `DeviceListResult`），包含 `list` 和 `total` 字段

## Goals / Non-Goals

**Goals:**
- 创建操作日志实体，包含操作时间、操作类型、操作对象信息、操作结果、操作失败原因、操作者 IP 等字段
- objectName 独立为数据库列用于关键字搜索，objectExtra 使用 JSON 存储额外信息
- 新增 description 字段记录具体操作内容
- 新增 operatorIp 字段记录操作者 IP
- 提供 `@OperationLog` 注解 + AOP 实现快速添加操作日志功能
- 提供 `OperationLogHelper` 静态工具类简化调用
- 实现异步写入机制，操作日志先放入内存缓存，由独立线程持续写入数据库
- 缓存数据持久化到磁盘文件，避免重启丢失
- 提供批量查询接口，支持按对象信息关键字、操作时间范围、操作结果进行过滤
- 支持排序（operationTime、objectName、operatorIp、description）
- 提供导出到 Excel 功能
- 操作日志持久化到 SQLite 数据库
- 对常用查询字段建立索引

**Non-Goals:**
- 不支持操作日志删除功能
- 不提供操作日志修改功能
- 不与现有的审计系统集成
- 暂不考虑数字签名或只读存储

## Decisions

### Decision 1: 操作日志表结构

**选择:** 使用单一表 `operation_log`，objectName 作为独立列，objectExtra 使用 JSON 存储额外信息

**理由:**
- objectName 作为独立列可以建索引，提升关键字搜索性能
- JSON 字段存储额外信息，保持扩展性
- 与现有代码风格一致

**替代方案考虑:**
- 全部 JSON 存储 → 关键字搜索无法使用索引
- 完全扁平化 → 字段过多，难以维护

### Decision 2: 异步写入机制

**选择:** 内存缓存 + 独立线程 + 磁盘持久化

**理由:**
- 内存缓存避免频繁写库，提升性能
- 独立线程持续写入保证日志最终一致性
- 磁盘持久化避免重启丢失

**实现细节:**
- 缓存使用 ConcurrentLinkedQueue 存储
- 每添加一条日志同时写入磁盘文件（异步）
- 写入线程定期批量从缓存读取并写入数据库
- 批量写入成功后清理磁盘文件对应数据

### Decision 3: 操作日志记录方式

**选择:** 提供注解 + 静态工具类两种方式

**理由:**
- 注解方式：声明式，简洁，适合大多数场景
- 静态工具类：编程式，灵活，适合复杂场景
- 两者互补，满足不同需求

**替代方案考虑:**
- 只提供注解 → 复杂场景不够灵活
- 只提供工具类 → 使用不够简洁

### Decision 4: 注解参数提取

**选择:** 使用 Spring SpEL 表达式

**理由:**
- 功能强大，支持各种表达式
- 与 Spring 生态集成良好
- 社区认可度高

**示例:**
```java
@OperationLog(
    operationType = OperationType.CREATE,
    objectId = "#device.ip",
    objectName = "#device.name",
    description = "'创建设备: ' + #device.name"
)
```

### Decision 5: 查询接口分页参数设计

**选择:** 与现有设备查询接口保持一致，使用 `offset` 和 `limit` 参数

**理由:** 与现有代码风格保持一致，减少学习成本，便于前端调用

### Decision 6: 排序实现

**选择:** 支持指定排序字段和排序方向

**理由:** 提供灵活性，满足不同场景需求

**排序字段:** operationTime、objectName、operatorIp、description

## Risks / Trade-offs

**风险 1:** 操作日志数据量增长可能导致查询性能下降
**缓解措施:**
- 对常用查询字段建立索引
- 分页大小限制为 100 条
- 考虑定期归档历史数据（未来）

**风险 2:** 磁盘持久化文件过大
**缓解措施:**
- 批量写入成功后清理磁盘文件
- 限制磁盘文件大小（可配置）
- 启动时检查文件大小，异常则告警

**风险 3:** 异步写入可能导致主线程异常时日志丢失
**缓解措施:**
- 缓存操作时同步写磁盘
- 定期强制刷盘（可配置）
- 关闭应用前等待缓存写入完成

**风险 4:** JSON 格式存储额外信息可能导致存储空间增加
**缓解措施:**
- 仅存储必要的额外信息，避免存储完整对象

## Migration Plan

1. 创建数据库表和索引
2. 实现数据模型和枚举
3. 实现 Repository 层
4. 实现缓存和异步写入机制
5. 实现 Service 层
6. 实现静态工具类
7. 实现注解和 AOP
8. 实现 REST API
9. 编写测试
10. 在现有模块中集成