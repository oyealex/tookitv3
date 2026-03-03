## Why

当前系统缺乏设备管理能力，无法统一管理各类设备（存储设备、服务器等）的基本信息。需要提供一套完整的设备管理功能，支持设备的增删改查、批量操作和Excel导入导出，为后续扩展不同类型设备奠定基础。

## What Changes

- 新增设备管理模块，支持设备的完整生命周期管理
- 新增单个设备添加和批量添加功能
- 新增设备查询功能（单个详情、批量列表、条件筛选）
- 新增设备删除功能（单个删除、批量删除）
- 新增设备Excel导入导出功能
- 新增SQLite数据库集成，作为设备数据持久化存储
- 新建设备基础模型，支持未来扩展不同类型设备子类

## Capabilities

### New Capabilities
- `device-management`: 设备核心管理功能，包括设备CRUD操作、批量操作、数据模型设计
- `device-excel-io`: 设备Excel导入导出功能，支持批量导出设备到Excel、从Excel批量导入设备
- `sqlite-integration`: SQLite数据库集成，提供数据持久化存储能力

### Modified Capabilities
<!-- 无现有能力需要修改 -->

## Impact

- **新增代码**：
  - `model/Device.java` - 设备基础实体类
  - `repository/DeviceRepository.java` - 设备数据访问层
  - `service/DeviceService.java` - 设备业务逻辑层
  - `controller/DeviceController.java` - 设备REST API控制器
  - `config/SQLiteConfig.java` - SQLite数据源配置
  - Excel导入导出相关工具类

- **依赖变更**：
  - 新增SQLite JDBC驱动依赖
  - 新增Apache POI或EasyExcel依赖（用于Excel操作）

- **API变更**：
  - 新增 `/api/devices` 系列RESTful API端点