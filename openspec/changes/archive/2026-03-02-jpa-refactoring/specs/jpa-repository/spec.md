## ADDED Requirements

### Requirement: Device数据持久化使用JPA
系统 SHALL 使用 Spring Data JPA 进行设备数据的持久化操作，替代现有的 JdbcTemplate 实现。

#### Scenario: 插入单个设备
- **WHEN** 调用 DeviceRepository.insert(Device) 方法
- **THEN** 设备数据应正确保存到数据库

#### Scenario: 批量插入设备
- **WHEN** 调用 DeviceRepository.batchInsert(List<Device>) 方法
- **THEN** 所有设备应正确批量保存到数据库

#### Scenario: 根据IP查询设备
- **WHEN** 调用 DeviceRepository.findByIp(String ip) 方法
- **THEN** 返回对应IP的设备（如果存在），否则返回空Optional

#### Scenario: 分页查询设备列表
- **WHEN** 调用 DeviceRepository.findAll(int offset, int limit, 各种查询条件) 方法
- **THEN** 返回符合条件且分页后的设备列表

#### Scenario: 统计设备总数
- **WHEN** 调用 DeviceRepository.count() 方法
- **THEN** 返回设备总数量

#### Scenario: 按条件统计设备数量
- **WHEN** 调用 DeviceRepository.count(String ip, String name, DeviceType type, String model, String version) 方法
- **THEN** 返回符合筛选条件的设备数量

#### Scenario: 更新设备信息
- **WHEN** 调用 DeviceRepository.update(Device) 方法
- **THEN** 设备信息应正确更新到数据库

#### Scenario: 根据IP删除设备
- **WHEN** 调用 DeviceRepository.deleteByIp(String ip) 方法
- **THEN** 对应IP的设备应从数据库中删除

#### Scenario: 批量删除设备
- **WHEN** 调用 DeviceRepository.deleteByIps(List<String> ips) 方法
- **THEN** 所有指定IP的设备应从数据库中删除

#### Scenario: 检查设备是否存在
- **WHEN** 调用 DeviceRepository.existsByIp(String ip) 方法
- **THEN** 返回布尔值表示设备是否存在

### Requirement: OperationLog数据持久化使用JPA
系统 SHALL 使用 Spring Data JPA 进行操作日志数据的持久化操作，替代现有的 JdbcTemplate 实现。

#### Scenario: 插入单条操作日志
- **WHEN** 调用 OperationLogRepository.insert(OperationLog) 方法
- **THEN** 操作日志应正确保存到数据库

#### Scenario: 批量插入操作日志
- **WHEN** 调用 OperationLogRepository.batchInsert(List<OperationLog>) 方法
- **THEN** 所有操作日志应正确批量保存到数据库

#### Scenario: 分页查询操作日志
- **WHEN** 调用 OperationLogRepository.findAll(int offset, int limit, 各种查询条件) 方法
- **THEN** 返回符合条件且分页后的操作日志列表

#### Scenario: 统计操作日志总数
- **WHEN** 调用 OperationLogRepository.count(...) 方法
- **THEN** 返回符合筛选条件的操作日志总数量

#### Scenario: 根据ID查询操作日志
- **WHEN** 调用 OperationLogRepository.findById(Long id) 方法
- **THEN** 返回对应ID的操作日志（如果存在），否则返回空Optional

### Requirement: 业务逻辑保持不变
重构后的Repository实现 SHALL 保持与原实现完全相同的业务行为，确保Service层无需修改。

#### Scenario: Service层功能测试通过
- **WHEN** 运行现有的DeviceService和OperationLogService的单元测试
- **THEN** 所有测试用例应通过

#### Scenario: API端点功能验证
- **WHEN** 调用现有的REST API端点
- **THEN** 返回结果应与重构前完全一致

### Requirement: TDD开发流程
重构过程 SHALL 遵循测试驱动开发模式，先编写测试用例确保功能不变，再进行实现。

#### Scenario: 先有测试再有实现
- **WHEN** 开始重构某个Repository方法
- **THEN** 应先编写或运行对应的测试用例，确保测试失败后再编写实现代码

#### Scenario: 测试覆盖所有CRUD操作
- **WHEN** 编写测试用例时
- **THEN** 应覆盖所有现有的数据库操作方法（insert, update, delete, find, batch等）

### Requirement: Entity与Model转换
系统 SHALL 在数据访问层实现Entity到Model的自动转换，确保Service层无感知。

#### Scenario: Repository返回Model对象
- **WHEN** Service调用Repository的查询方法
- **THEN** 返回的是业务Model对象，而非JPA Entity

#### Scenario: 插入/更新使用Model对象
- **WHEN** Service传入Model对象进行插入或更新
- **THEN** Repository内部转换为Entity进行持久化

### Requirement: 接口返回类型兼容
重构后的Repository SHALL 保持与原实现兼容的返回类型。

#### Scenario: insert方法返回void
- **WHEN** 调用 EntityManager.persist() 进行插入
- **THEN** Service层调用处无需依赖返回值

#### Scenario: update方法自动更新timestamp
- **WHEN** 调用 EntityManager.merge() 进行更新
- **THEN** updated_at字段自动更新为当前时间