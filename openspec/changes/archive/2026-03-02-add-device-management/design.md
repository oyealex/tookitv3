## Context

当前系统是一个基于 Spring Boot 的工具箱应用，使用 Java 21、Maven、Lombok 构建。系统已有基本的 REST API 框架（统一响应格式 `Result<T>`、全局异常处理）和健康检查端点。数据持久化目前未集成，本设计将引入 SQLite 作为嵌入式数据库存储设备信息。

设备管理需要支持多种设备类型（存储设备、服务器设备等），设备具有公共属性（IP、类型、型号、版本、登录用户名、登录密码），未来可能扩展子类。

## Goals / Non-Goals

**Goals:**
- 设计可扩展的设备数据模型，支持未来添加不同类型设备子类
- 集成 SQLite 作为嵌入式数据库
- 实现设备 CRUD 操作，支持单个和批量操作
- 实现设备 Excel 导入导出功能
- 提供清晰的 RESTful API 接口（带版本控制）
- 支持中英文国际化

**Non-Goals:**
- 不实现用户权限控制
- 不实现设备连接测试或监控功能
- 不实现设备分组管理
- 不支持其他数据库（如 MySQL、PostgreSQL）
- 不实现软删除机制
- 不实现设备状态管理

## Decisions

### 1. 设备模型设计：基础表 + 独有属性扩展表

**选择**: 基础设备表存储公共属性，未来扩展时根据需求选择策略

**扩展策略**:
- **公共属性扩展**: 直接在设备表添加新列
- **独有属性扩展**: 新建设备类型专属表，通过外键关联设备表

**理由**:
- 结构清晰，易于维护和查询
- 独有属性表隔离不同类型设备的特殊字段
- 避免 JSON 字段的查询和索引性能问题

**基础模型字段**:
```
Device (公共属性):
- ip: String (主键，IPv4格式)
- name: String (设备名称，可选，最长120字符，留空时使用"类型+IP")
- type: DeviceType (设备类型枚举：STORAGE/SERVER/NETWORK等)
- model: String (型号)
- version: String (版本)
- username: String (登录用户名)
- password: String (登录密码，非空，明文存储)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

**设备类型枚举**:
```java
public enum DeviceType {
    STORAGE,   // 存储设备
    SERVER,    // 服务器
    NETWORK    // 网络设备
}
```

**扩展示例** (未来独有属性表):
```
StorageDevice (存储设备独有属性):
- deviceIp: String (外键关联 Device.ip)
- capacity: Long (存储容量，GB)
- raidLevel: String (RAID级别)

ServerDevice (服务器设备独有属性):
- deviceIp: String (外键关联 Device.ip)
- cpuCores: Integer (CPU核数)
- memorySize: Long (内存大小，GB)
```

**名称规则**:
- 可选字段，用户可自定义
- 最长 120 个可见字符
- 留空时自动填充为 `类型名称+IP`，如 "STORAGE-192.168.1.1"

### 2. 数据库选型：SQLite

**选择**: SQLite (嵌入式数据库)

**理由**:
- 轻量级，无需独立数据库服务
- 适合工具箱类应用的本地数据存储
- 零配置，数据文件便于备份和迁移

**依赖**:
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>
```

**表结构**:
```sql
CREATE TABLE device (
    ip VARCHAR(15) PRIMARY KEY,
    name VARCHAR(120),
    type VARCHAR(20) NOT NULL,
    model VARCHAR(100),
    version VARCHAR(50),
    username VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_device_name ON device(name);
CREATE INDEX idx_device_type ON device(type);
CREATE INDEX idx_device_model ON device(model);
CREATE INDEX idx_device_version ON device(version);
```

### 3. Excel 处理：EasyExcel

**选择**: EasyExcel (阿里开源)

**理由**:
- 内存占用低，适合大数据量导入导出
- API 简洁易用
- Spring Boot 集成方便

**备选方案**:
- Apache POI：内存占用高，大数据量性能差

**依赖**:
```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>easyexcel</artifactId>
    <version>3.3.3</version>
</dependency>
```

**Excel 导出字段**（不含密码）:
| 列名 | 字段 | 说明 |
|------|------|------|
| 设备名称 | name | |
| IP地址 | ip | |
| 设备类型 | type | STORAGE/SERVER/NETWORK |
| 型号 | model | |
| 版本 | version | |
| 登录用户名 | username | |
| 创建时间 | createdAt | yyyy-MM-dd HH:mm:ss |
| 更新时间 | updatedAt | yyyy-MM-dd HH:mm:ss |

**Excel 模板**:
- 包含表头行
- 第二行为字段说明行（如："必填，IPv4格式"、"选填，最长120字符"）
- 第三行为示例数据行

### 4. 分层架构

**选择**: Controller → Service → Repository 三层架构

**结构**:
```
controller/
  DeviceController.java          # REST API 入口
service/
  DeviceService.java             # 业务逻辑
  DeviceExcelService.java        # Excel 导入导出
repository/
  DeviceRepository.java          # 数据访问 (Spring Data JDBC)
model/
  Device.java                    # 实体类
  DeviceType.java                # 设备类型枚举
  dto/
    DeviceCreateDTO.java         # 创建请求DTO
    DeviceUpdateDTO.java         # 更新请求DTO
    DeviceQueryDTO.java          # 查询条件DTO
    BatchResultDTO.java          # 批量操作结果DTO
config/
  SQLiteConfig.java              # SQLite 数据源配置
util/
  IpValidator.java               # IP地址校验工具
```

### 5. API 设计

**RESTful 端点**（带版本控制）:
```
POST   /api/v1/devices              # 添加单个设备
POST   /api/v1/devices/batch        # 批量添加设备
GET    /api/v1/devices/{ip}         # 查询单个设备详情
GET    /api/v1/devices              # 批量查询设备列表
PUT    /api/v1/devices/{ip}         # 更新设备信息
DELETE /api/v1/devices/{ip}         # 删除单个设备
DELETE /api/v1/devices/batch        # 批量删除设备
GET    /api/v1/devices/export       # 导出设备到Excel
GET    /api/v1/devices/template     # 下载导入模板
POST   /api/v1/devices/import       # 从Excel导入设备
```

**分页参数**: 使用 `offset` 和 `limit`
```
GET /api/v1/devices?offset=0&limit=20&type=SERVER&name=xxx
```

**批量操作响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "successCount": 95,
    "failCount": 5,
    "failReasons": [
      { "row": 3, "ip": "192.168.1.1", "reason": "IP地址格式错误" },
      { "row": 7, "ip": "192.168.1.2", "reason": "设备类型无效" }
      // 最多显示 top 10 条失败原因
    ]
  }
}
```

### 6. 数据校验规则

**IP地址校验**:
- 格式：IPv4（如 192.168.1.1）
- 正则：`^((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\.){3}(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)$`

**名称校验**:
- 长度：0-120 个可见字符
- 可见字符定义：ASCII 32-126

**密码校验**:
- 仅要求非空

**设备类型校验**:
- 必须为枚举值：STORAGE、SERVER、NETWORK

### 7. SQL 注入防护

**措施**:
- 使用 Spring Data JDBC 的参数化查询
- 禁止字符串拼接 SQL
- 所有查询参数使用 `?` 占位符或 `@Param` 注解

### 8. 国际化支持

**支持语言**: 中文（默认）、英文

**实现方式**:
- 使用 Spring MessageSource
- 错误消息根据 `Accept-Language` 请求头返回对应语言
- 消息文件：`messages_zh_CN.properties`、`messages_en_US.properties`

**示例消息**:
```properties
# messages_zh_CN.properties
error.device.not.found=设备不存在
error.device.ip.invalid=IP地址格式错误
error.device.ip.duplicate=IP地址已存在

# messages_en_US.properties
error.device.not.found=Device not found
error.device.ip.invalid=Invalid IP address format
error.device.ip.duplicate=IP address already exists
```

### 9. 规格限制

| 限制项 | 限制值 | 说明 |
|--------|--------|------|
| 设备总数上限 | 1000 台 | 系统最多支持管理 1000 台设备 |
| 批量查询 | 1-100 条/次 | 客户端可指定，默认 20，最大 100 |
| 批量删除 | 100 台/次 | 单次批量删除最多处理 100 台设备 |
| 批量添加 | 100 台/次 | 单次批量添加最多处理 100 台设备 |
| Excel 导出 | 无限制 | 支持导出全部设备 |
| Excel 导入 | 无限制 | 支持导入任意数量设备（受设备总数上限约束） |
| 失败原因显示 | Top 10 | 批量操作失败原因最多显示前 10 条 |

**实现方式**:
- 设备总数：添加设备前检查 `SELECT COUNT(*) FROM device`
- 批量操作限制：在 DTO 中使用 `@Size(max=100)` 校验
- Excel 导入：流式处理，超过总数上限时返回错误提示
- 幂等性：IP 作为主键，数据库层面保证幂等

### 10. 单元测试

**覆盖率要求**: 核心业务逻辑分支覆盖率 > 90%

**测试范围**:
- DeviceService：所有业务方法
- DeviceExcelService：导入导出逻辑
- IpValidator：IP 格式校验
- Controller 层集成测试

**测试框架**:
- JUnit 5
- Mockito
- Spring Boot Test

## Risks / Trade-offs

| 风险 | 缓解措施 |
|------|----------|
| SQLite 并发写入性能有限 | 使用 WAL 模式提升并发性能；批量操作使用事务 |
| 未来设备类型扩展需新建表 | 预先规划扩展流程，由实现层面处理 |
| 密码明文存储 | 后续版本实现加密存储 |
| Excel 导入大数据量内存问题 | 使用 EasyExcel 流式读取 |

## Migration Plan

1. **初始化**: 应用启动时自动创建 SQLite 数据库文件和表结构
2. **数据迁移**: 首次部署无历史数据，无需迁移
3. **回滚策略**: 删除数据库文件即可重置