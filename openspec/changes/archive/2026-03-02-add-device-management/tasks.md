## 1. 项目基础配置

- [x] 1.1 添加 SQLite JDBC 依赖到 pom.xml
- [x] 1.2 添加 EasyExcel 依赖到 pom.xml
- [x] 1.3 创建 SQLite 数据源配置类 SQLiteConfig.java
- [x] 1.4 配置 application.yml 数据库连接信息
- [x] 1.5 实现数据库初始化脚本（建表、索引）

## 2. 数据模型层

- [x] 2.1 创建设备类型枚举 DeviceType.java
- [x] 2.2 创建设备实体类 Device.java
- [x] 2.3 创建设备创建请求 DTO DeviceCreateDTO.java
- [x] 2.4 创建设备更新请求 DTO DeviceUpdateDTO.java
- [x] 2.5 创建设备查询条件 DTO DeviceQueryDTO.java
- [x] 2.6 创建批量操作结果 DTO BatchResultDTO.java
- [x] 2.7 创建 IP 地址校验工具类 IpValidator.java

## 3. 数据访问层

- [x] 3.1 创建 DeviceRepository 接口
- [x] 3.2 实现设备新增方法
- [x] 3.3 实现设备批量新增方法
- [x] 3.4 实现设备查询方法（单个、列表、条件筛选、分页）
- [x] 3.5 实现设备更新方法
- [x] 3.6 实现设备删除方法（单个、批量）
- [x] 3.7 实现设备总数统计方法

## 4. 业务逻辑层

- [x] 4.1 创建 DeviceService 接口
- [x] 4.2 实现添加单个设备方法（含 IP 校验、名称默认值、总数限制）
- [x] 4.3 实现批量添加设备方法（含批量结果返回）
- [x] 4.4 实现查询单个设备方法
- [x] 4.5 实现批量查询设备列表方法（含分页、筛选）
- [x] 4.6 实现更新设备方法
- [x] 4.7 实现删除单个设备方法
- [x] 4.8 实现批量删除设备方法（含批量结果返回）

## 5. Excel 导入导出服务

- [x] 5.1 创建 DeviceExcelService 类
- [x] 5.2 创建 Excel 导出监听器/处理器
- [x] 5.3 实现设备导出到 Excel 方法（不含密码）
- [x] 5.4 创建 Excel 导入监听器/处理器
- [x] 5.5 实现从 Excel 导入设备方法（含错误行号）
- [x] 5.6 实现 Excel 模板下载方法（含字段说明、示例数据）

## 6. REST API 控制器

- [x] 6.1 创建 DeviceController 类
- [x] 6.2 实现 POST /api/v1/devices 添加单个设备
- [x] 6.3 实现 POST /api/v1/devices/batch 批量添加设备
- [x] 6.4 实现 GET /api/v1/devices/{ip} 查询单个设备
- [x] 6.5 实现 GET /api/v1/devices 批量查询设备列表
- [x] 6.6 实现 PUT /api/v1/devices/{ip} 更新设备
- [x] 6.7 实现 DELETE /api/v1/devices/{ip} 删除单个设备
- [x] 6.8 实现 DELETE /api/v1/devices/batch 批量删除设备
- [x] 6.9 实现 GET /api/v1/devices/export 导出设备 Excel
- [x] 6.10 实现 GET /api/v1/devices/template 下载导入模板
- [x] 6.11 实现 POST /api/v1/devices/import 导入设备 Excel

## 7. 国际化支持

- [x] 7.1 配置 MessageSource Bean
- [x] 7.2 创建中文消息文件 messages_zh_CN.properties
- [x] 7.3 创建英文消息文件 messages_en_US.properties
- [x] 7.4 创建国际化工具类 I18nUtil.java
- [x] 7.5 在业务逻辑中集成国际化消息

## 8. 异常处理

- [x] 8.1 定义设备相关错误码枚举 DeviceErrorCode.java
- [x] 8.2 创建设备业务异常类 DeviceException.java
- [x] 8.3 扩展 GlobalExceptionHandler 处理设备异常
- [x] 8.4 添加参数校验异常处理

## 9. 单元测试

- [x] 9.1 创建 DeviceService 测试类
- [x] 9.2 编写添加设备测试用例
- [x] 9.3 编写批量添加设备测试用例
- [x] 9.4 编写查询设备测试用例
- [x] 9.5 编写更新设备测试用例
- [x] 9.6 编写删除设备测试用例
- [x] 9.7 创建 IpValidator 测试类
- [x] 9.8 编写 IP 校验测试用例
- [x] 9.9 创建 DeviceExcelService 测试类
- [x] 9.10 编写 Excel 导入导出测试用例
- [x] 9.11 确保核心逻辑分支覆盖率 > 90%

## 10. 集成测试

- [x] 10.1 创建 DeviceController 集成测试类
- [x] 10.2 编写 API 端点集成测试用例
- [x] 10.3 编写分页和筛选集成测试用例