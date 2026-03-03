# SQLite Integration

## Purpose

本能力提供 SQLite 数据库集成，用于存储和管理设备数据。

## Requirements

### Requirement: SQLite 数据库集成
系统 SHALL 集成 SQLite 作为嵌入式数据库存储设备数据。

#### Scenario: 应用启动时初始化数据库
- **WHEN** 应用启动
- **THEN** 自动创建 SQLite 数据库文件（如不存在）
- **THEN** 自动创建设备表结构（如不存在）

#### Scenario: 数据库文件位置
- **WHEN** 应用启动
- **THEN** 数据库文件存储在应用目录下的 `data/toolbox.db`

### Requirement: 设备表结构
系统 SHALL 创建设备表（device）存储设备基本信息。

#### Scenario: 设备表字段
- **WHEN** 创建设备表
- **THEN** 表包含字段：ip（主键）、name、type、model、version、username、password、created_at、updated_at

#### Scenario: 字段约束
- **WHEN** 创建设备表
- **THEN** ip 字段设置为主键
- **THEN** type、password 字段设置为 NOT NULL

### Requirement: 索引设计
系统 SHALL 为设备表创建必要的索引以优化查询性能。

#### Scenario: 创建索引
- **WHEN** 创建设备表
- **THEN** 为 name、type、model、version 字段创建索引

### Requirement: 数据库配置
系统 SHALL 通过配置文件管理 SQLite 数据库连接。

#### Scenario: 数据源配置
- **WHEN** 应用启动
- **THEN** 使用 Spring DataSource 配置 SQLite 连接

#### Scenario: 连接池配置
- **WHEN** 配置数据库连接
- **THEN** 使用 HikariCP 连接池，配置适当的连接数

### Requirement: 数据库性能优化
系统 SHALL 启用 SQLite WAL 模式提升并发性能。

#### Scenario: 启用 WAL 模式
- **WHEN** 数据库初始化
- **THEN** 执行 `PRAGMA journal_mode=WAL` 启用 WAL 模式

#### Scenario: 批量操作事务处理
- **WHEN** 执行批量添加、批量删除操作
- **THEN** 使用事务确保数据一致性
