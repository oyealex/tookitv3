# Operation Log

## Purpose

本能力提供操作日志功能，用于记录和查询系统中的业务操作行为，支持问题追溯和审计合规需求。

## Requirements

### Requirement: 操作日志数据模型
系统 SHALL 提供操作日志实体类，包含以下公共属性：
- `id`: 主键，自增
- `operationTime`: 操作时间，精确到毫秒
- `operationType`: 操作类型枚举（CREATE/UPDATE/DELETE/IMPORT/EXPORT）
- `objectType`: 操作对象类型（如 DEVICE）
- `objectId`: 操作对象标识（如设备 IP）
- `objectName`: 操作对象名称/关键字，独立数据库列，用于关键字搜索
- `objectExtra`: 操作对象额外信息，JSON 格式，存储非关键字段
- `description`: 操作描述，记录具体做了什么操作
- `result`: 操作结果枚举（SUCCESS/FAILURE）
- `failureReason`: 失败原因，仅在结果为 FAILURE 时有值
- `operator`: 操作人（可选）
- `operatorIp`: 操作者 IP 地址
- `createdAt`: 记录创建时间

#### Scenario: 记录成功操作
- **WHEN** 记录一次成功的操作
- **THEN** 系统保存操作日志，result 字段为 SUCCESS，failureReason 字段为空

#### Scenario: 记录失败操作
- **WHEN** 记录一次失败的操作
- **THEN** 系统保存操作日志，result 字段为 FAILURE，failureReason 字段包含失败原因

#### Scenario: 关键字搜索
- **WHEN** 按 objectName 关键字搜索操作日志
- **THEN** 返回匹配的操作日志

### Requirement: 操作日志类型枚举
系统 SHALL 提供操作类型枚举 `OperationType`，包含以下值：
- `CREATE`: 创建操作
- `UPDATE`: 更新操作
- `DELETE`: 删除操作
- `IMPORT`: 导入操作
- `EXPORT`: 导出操作

#### Scenario: 有效操作类型
- **WHEN** 使用有效的操作类型记录日志
- **THEN** 接受该类型值

### Requirement: 操作结果枚举
系统 SHALL 提供操作结果枚举 `OperationResult`，包含以下值：
- `SUCCESS`: 操作成功
- `FAILURE`: 操作失败

### Requirement: 操作日志注解记录
系统 SHALL 提供 `@OperationLog` 注解，结合 Spring AOP 实现快速添加操作日志记录功能。

#### Scenario: 使用注解记录操作
- **WHEN** 在方法上标注 `@OperationLog` 注解并配置相关参数
- **THEN** 方法执行后自动记录操作日志

#### Scenario: 注解配置操作类型
- **WHEN** 使用 `@OperationLog(operationType = OperationType.CREATE)`
- **THEN** 日志记录为指定的创建操作类型

#### Scenario: 注解自动提取对象信息
- **WHEN** 注解配置 `objectId = "#device.ip"`, `objectName = "#device.name"`
- **THEN** 自动从方法参数中提取值

#### Scenario: 注解配置结果提取
- **WHEN** 注解配置 `result = "#result"`, `failureReason = "#e.message"`
- **THEN** 自动从返回值或异常中提取结果信息

### Requirement: 操作日志静态工具类
系统 SHALL 提供 `OperationLogHelper` 静态工具类，方便其他模块快速记录操作日志。

#### Scenario: 使用静态方法记录日志
- **WHEN** 调用 `OperationLogHelper.log(operationType, objectType, objectId, objectName, description, result)`
- **THEN** 系统保存操作日志

#### Scenario: 异步记录日志
- **WHEN** 调用 `OperationLogHelper.logAsync(...)`
- **THEN** 系统异步保存操作日志，不阻塞主流程

### Requirement: 操作日志异步写入
系统 SHALL 实现异步写入机制，操作日志先放入内存缓存，由独立线程持续写入数据库。

#### Scenario: 异步写入操作日志
- **WHEN** 调用异步方法记录操作日志
- **THEN** 操作日志先放入内存缓存，立即返回

#### Scenario: 独立线程持续写入
- **WHEN** 内存缓存中有待写入的操作日志
- **THEN** 独立线程持续将缓存中的日志写入数据库

#### Scenario: 缓存持久化
- **WHEN** 添加操作日志到缓存
- **THEN** 同时将日志写入磁盘文件，避免重启或意外退出导致缓存丢失

#### Scenario: 启动时恢复缓存
- **WHEN** 应用启动
- **THEN** 从磁盘文件恢复未写入数据库的操作日志

### Requirement: 数据库索引
系统 SHALL 对常用查询字段建立索引，确保查询性能。

#### Scenario: 操作日志表索引
- **WHEN** 创建 operation_log 表
- **THEN** 建立以下索引：
  - `(operationTime DESC)` - 按操作时间倒序
  - `(objectType, objectId)` - 按对象类型和ID查询
  - `(result)` - 按操作结果筛选
  - `(objectName)` - 按对象名称关键字搜索

### Requirement: 添加操作日志
系统 SHALL 提供便捷的接口供其他模块添加操作日志。

#### Scenario: 同步添加操作日志
- **WHEN** 调用操作日志服务同步添加方法
- **THEN** 系统同步保存操作日志到数据库

#### Scenario: 异步添加操作日志
- **WHEN** 调用操作日志服务异步添加方法
- **THEN** 系统异步保存操作日志，不阻塞主流程

### Requirement: 批量查询操作日志
系统 SHALL 提供 REST API 批量查询操作日志，支持分页和条件筛选，客户端可指定返回数量（1-100）。

#### Scenario: 分页查询操作日志
- **WHEN** 请求操作日志列表并指定 offset 和 limit 参数
- **THEN** 返回分页结果，包含总数、当前页数据

#### Scenario: 按对象信息关键字筛选
- **WHEN** 按 objectName 关键字筛选
- **THEN** 返回匹配的操作日志列表

#### Scenario: 按操作时间范围筛选
- **WHEN** 指定开始时间和结束时间进行筛选
- **THEN** 返回指定时间范围内的操作日志

#### Scenario: 按操作结果筛选
- **WHEN** 按操作结果（SUCCESS/FAILURE）筛选
- **THEN** 返回指定结果的操作日志

#### Scenario: 组合条件筛选
- **WHEN** 同时指定多个筛选条件
- **THEN** 返回满足所有条件的操作日志

#### Scenario: 默认分页参数
- **WHEN** 未指定分页参数
- **THEN** 使用默认值 offset=0，limit=20

#### Scenario: limit 超过最大值
- **WHEN** 请求的 limit 超过 100
- **THEN** 返回错误提示 "单次查询数量不能超过 100 条"

### Requirement: 操作日志排序
系统 SHALL 支持按多个字段进行排序查询。

#### Scenario: 按操作时间排序
- **WHEN** 指定 sortBy=operationTime&sortOrder=desc
- **THEN** 返回按操作时间倒序的日志列表

#### Scenario: 按操作对象排序
- **WHEN** 指定 sortBy=objectName&sortOrder=asc
- **THEN** 返回按对象名称正序的日志列表

#### Scenario: 按操作者IP排序
- **WHEN** 指定 sortBy=operatorIp&sortOrder=asc
- **THEN** 返回按操作者IP正序的日志列表

#### Scenario: 按操作描述排序
- **WHEN** 指定 sortBy=description&sortOrder=asc
- **THEN** 返回按操作描述正序的日志列表

### Requirement: 导出操作日志
系统 SHALL 提供 REST API 导出操作日志为 Excel 文件。

#### Scenario: 导出全部操作日志
- **WHEN** 请求导出操作日志不指定筛选条件
- **THEN** 导出全部操作日志为 Excel 文件

#### Scenario: 导出筛选后的操作日志
- **WHEN** 请求导出操作日志并指定筛选条件
- **THEN** 导出符合条件的数据为 Excel 文件

#### Scenario: 导出分页参数
- **WHEN** 指定导出时的 offset 和 limit
- **THEN** 导出指定范围的数据

### Requirement: 操作日志 API 版本控制
系统 SHALL 对所有操作日志 API 使用版本控制前缀 `/api/v1`。

#### Scenario: 使用版本化 API
- **WHEN** 访问操作日志 API
- **THEN** URL 路径以 `/api/v1/operation-logs` 开头

### Requirement: 操作日志查询返回完整信息
系统 SHALL 在查询返回结果中包含操作日志的全部信息。

#### Scenario: 查询返回完整日志
- **WHEN** 查询操作日志列表
- **THEN** 返回每条日志的完整字段信息

### Requirement: 操作日志不支持删除
系统 SHALL 不提供删除操作日志的接口。

#### Scenario: 尝试删除操作日志
- **WHEN** 发送 DELETE 请求到操作日志端点
- **THEN** 返回 405 Method Not Allowed 错误

### Requirement: 操作日志国际化
系统 SHALL 支持操作日志描述的国际化，根据当前语言环境返回对应语言的描述文本。

#### Scenario: 中文环境记录日志
- **WHEN** 当前语言环境为中文 (zh_CN)，记录操作日志
- **THEN** description 字段使用中文描述

#### Scenario: 英文环境记录日志
- **WHEN** 当前语言环境为英文 (en_US)，记录操作日志
- **THEN** description 字段使用英文描述

#### Scenario: 使用消息键记录国际化描述
- **WHEN** 使用消息键（如 "operation.device.create"）记录日志描述
- **THEN** 系统根据当前语言环境自动获取对应语言的描述

#### Scenario: 获取当前语言环境
- **WHEN** 记录操作日志时
- **THEN** 使用 `LocaleContextHolder.getLocale()` 获取当前语言环境

#### Scenario: 未来支持语言切换接口
- **WHEN** 未来增加语言切换 API
- **THEN** 可通过 API 修改 `LocaleContextHolder` 的 locale 值

### Requirement: 操作日志语言切换（预留）
系统 SHALL 预留语言切换接口，未来可通过 API 修改当前语言环境。

#### Scenario: 查询当前语言环境
- **WHEN** 调用获取当前语言环境接口
- **THEN** 返回当前设置的 locale 值

#### Scenario: 设置语言环境
- **WHEN** 调用设置语言环境接口，传入有效的 locale 参数
- **THEN** 更新 `LocaleContextHolder` 的 locale 值，后续操作日志使用新的语言环境

#### Scenario: 无效的语言环境参数
- **WHEN** 设置不支持的语言环境
- **THEN** 返回错误提示，说明支持的语言环境列表

### Requirement: SQL 注入防护
系统 SHALL 使用参数化查询防止 SQL 注入。

#### Scenario: 参数化查询
- **WHEN** 执行数据库查询
- **THEN** 所有参数使用占位符传递，禁止字符串拼接 SQL