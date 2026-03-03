# Device Management

## Purpose

本能力提供设备管理功能，支持设备的增删改查、批量操作、条件筛选和分页查询。

## Requirements

### Requirement: 设备数据模型
系统 SHALL 提供设备实体类，包含以下公共属性：
- `ip`: 主键，IPv4 格式
- `name`: 设备名称，可选，最长 120 可见字符，留空时使用"类型+IP"
- `type`: 设备类型枚举（STORAGE/SERVER/NETWORK）
- `model`: 型号
- `version`: 版本
- `username`: 登录用户名
- `password`: 登录密码，非空
- `createdAt`: 创建时间
- `updatedAt`: 更新时间

#### Scenario: 创建设备实体
- **WHEN** 创建新的设备记录
- **THEN** 系统自动生成 `createdAt` 和 `updatedAt`

#### Scenario: 更新设备实体
- **WHEN** 更新设备记录
- **THEN** 系统自动更新 `updatedAt` 为当前时间

#### Scenario: 设备名称留空
- **WHEN** 创建设备时名称为空
- **THEN** 系统自动填充为"类型名称-IP"，如"STORAGE-192.168.1.1"

### Requirement: 设备类型枚举
系统 SHALL 提供设备类型枚举 `DeviceType`，包含以下值：
- `STORAGE`: 存储设备
- `SERVER`: 服务器
- `NETWORK`: 网络设备

#### Scenario: 校验有效设备类型
- **WHEN** 提交有效的设备类型（STORAGE/SERVER/NETWORK）
- **THEN** 接受该类型值

#### Scenario: 校验无效设备类型
- **WHEN** 提交无效的设备类型
- **THEN** 返回错误提示，说明有效类型列表

### Requirement: IP 地址校验
系统 SHALL 校验 IP 地址为有效的 IPv4 格式。

#### Scenario: 有效 IPv4 地址
- **WHEN** 提交有效的 IPv4 地址（如 192.168.1.1）
- **THEN** 接受该 IP 地址

#### Scenario: 无效 IP 格式
- **WHEN** 提交无效的 IP 地址格式
- **THEN** 返回错误提示 "IP 地址格式错误"

### Requirement: 设备名称校验
系统 SHALL 校验设备名称符合以下规则：
- 长度不超过 120 个可见字符
- 可见字符定义：ASCII 32-126

#### Scenario: 有效设备名称
- **WHEN** 提交有效的设备名称
- **THEN** 接受该名称

#### Scenario: 名称超长
- **WHEN** 提交超过 120 字符的设备名称
- **THEN** 返回错误提示 "设备名称长度不能超过 120 字符"

### Requirement: 设备总数限制
系统 SHALL 限制设备总数不超过 1000 台。

#### Scenario: 添加设备时检查总数
- **WHEN** 添加新设备时设备总数已达到 1000 台
- **THEN** 返回错误提示，拒绝添加操作

#### Scenario: 批量导入时检查总数
- **WHEN** 批量导入设备后总数将超过 1000 台
- **THEN** 返回错误提示，说明当前数量和可导入数量

### Requirement: 添加单个设备
系统 SHALL 提供 REST API 添加单个设备。

#### Scenario: 成功添加设备
- **WHEN** 提交有效的设备数据
- **THEN** 创建设备记录并返回设备详情

#### Scenario: 添加设备时 IP 重复
- **WHEN** 提交的 IP 地址已存在
- **THEN** 返回错误提示 "IP 地址已存在"

#### Scenario: 添加设备时必填字段缺失
- **WHEN** 提交的设备数据缺少必填字段（ip、type、password）
- **THEN** 返回参数校验错误

### Requirement: 批量添加设备
系统 SHALL 提供 REST API 批量添加设备，单次最多 100 台。

#### Scenario: 成功批量添加设备
- **WHEN** 提交有效的设备列表（数量≤100）
- **THEN** 返回成功数量、失败数量、失败原因（Top 10）

#### Scenario: 批量添加超过限制
- **WHEN** 提交的设备列表数量超过 100
- **THEN** 返回错误提示 "单次批量添加不能超过 100 台"

#### Scenario: 批量添加部分失败
- **WHEN** 批量添加列表中部分数据无效
- **THEN** 返回成功数量和失败详情，失败原因最多显示 Top 10

### Requirement: 查询单个设备详情
系统 SHALL 提供 REST API 根据 IP 查询单个设备详情。

#### Scenario: 成功查询设备
- **WHEN** 查询存在的设备 IP
- **THEN** 返回设备完整信息

#### Scenario: 查询不存在的设备
- **WHEN** 查询不存在的设备 IP
- **THEN** 返回 404 错误 "设备不存在"

### Requirement: 批量查询设备列表
系统 SHALL 提供 REST API 分页查询设备列表，支持条件筛选，客户端可指定返回数量（1-100）。

#### Scenario: 分页查询设备
- **WHEN** 请求设备列表并指定 offset 和 limit 参数
- **THEN** 返回分页结果，包含总数、当前页数据

#### Scenario: 条件筛选查询
- **WHEN** 按设备类型、名称、IP 等条件筛选
- **THEN** 返回符合条件的设备列表

#### Scenario: 默认分页参数
- **WHEN** 未指定分页参数
- **THEN** 使用默认值 offset=0，limit=20

#### Scenario: limit 超过最大值
- **WHEN** 请求的 limit 超过 100
- **THEN** 返回错误提示 "单次查询数量不能超过 100 条"

### Requirement: 更新设备信息
系统 SHALL 提供 REST API 根据 IP 更新设备信息。

#### Scenario: 成功更新设备
- **WHEN** 提交有效的设备更新数据
- **THEN** 更新设备记录并返回更新后的设备信息

#### Scenario: 更新不存在的设备
- **WHEN** 更新不存在的设备 IP
- **THEN** 返回 404 错误 "设备不存在"

### Requirement: 删除单个设备
系统 SHALL 提供 REST API 根据 IP 删除单个设备。

#### Scenario: 成功删除设备
- **WHEN** 删除存在的设备 IP
- **THEN** 删除设备记录并返回成功

#### Scenario: 删除不存在的设备
- **WHEN** 删除不存在的设备 IP
- **THEN** 返回 404 错误 "设备不存在"

### Requirement: 批量删除设备
系统 SHALL 提供 REST API 批量删除设备，单次最多 100 台。

#### Scenario: 成功批量删除设备
- **WHEN** 提交有效的设备 IP 列表（数量≤100）
- **THEN** 返回成功数量、失败数量、失败原因（Top 10）

#### Scenario: 批量删除超过限制
- **WHEN** 提交的设备 IP 列表数量超过 100
- **THEN** 返回错误提示 "单次批量删除不能超过 100 台"

#### Scenario: 批量删除部分失败
- **WHEN** 批量删除列表中部分 IP 不存在
- **THEN** 返回成功数量和失败详情，失败原因最多显示 Top 10

### Requirement: API 版本控制
系统 SHALL 对所有设备 API 使用版本控制前缀 `/api/v1`。

#### Scenario: 使用版本化 API
- **WHEN** 访问设备 API
- **THEN** URL 路径以 `/api/v1/devices` 开头

### Requirement: 国际化支持
系统 SHALL 支持中英文国际化错误消息。

#### Scenario: 中文错误消息
- **WHEN** 请求头 Accept-Language 为 zh-CN
- **THEN** 返回中文错误消息

#### Scenario: 英文错误消息
- **WHEN** 请求头 Accept-Language 为 en-US
- **THEN** 返回英文错误消息

### Requirement: SQL 注入防护
系统 SHALL 使用参数化查询防止 SQL 注入。

#### Scenario: 参数化查询
- **WHEN** 执行数据库查询
- **THEN** 所有参数使用占位符传递，禁止字符串拼接 SQL
