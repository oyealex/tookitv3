## 1. 设备锁定机制扩展

- [x] 1.1 扩展 DeviceLockService 接口，增加 lockDevices(ips, lockSource) 方法
- [x] 1.2 扩展 DeviceLockService 接口，增加 unlockBySource(lockSource) 方法
- [x] 1.3 扩展 DeviceLockService 接口，增加 getLockInfo(ip) 方法
- [x] 1.4 创建 DeviceLockInfo 模型类，包含 ip、lockedBy、lockedAt 字段
- [x] 1.5 在 DeviceServiceImpl 中使用 ConcurrentHashMap 实现设备锁定状态管理
- [x] 1.6 实现 DeviceLockService 接口方法
- [x] 1.7 修改 DeviceServiceImpl.updateDevice() 检查设备锁定状态
- [x] 1.8 修改 DeviceServiceImpl.deleteDevice() 检查设备锁定状态
- [x] 1.9 修改 DeviceServiceImpl.batchDeleteDevices() 检查设备锁定状态
- [x] 1.10 扩展 DeviceQueryDTO 支持 locked 参数筛选
- [x] 1.11 添加设备锁定相关的国际化消息

## 2. 场景配置模型与扫描

- [x] 2.1 创建 ScenarioStatus 枚举（IDLE, DEVICE_SELECTION, RUNNING, COMPLETED, FAILED）
- [x] 2.2 创建 StepStatus 枚举（PENDING, RUNNING, COMPLETED, FAILED, SKIPPED）
- [x] 2.3 创建 ScenarioStep 模型类，包含 toolId、name 字段
- [x] 2.4 创建 ScenarioConfig 模型类，包含 id、name、description、steps、allowSkipOnFailure、allowSkipStep 字段
- [x] 2.5 创建 ScenarioStepExecution 模型类，记录步骤执行状态
- [x] 2.6 创建 ScenarioExecution 模型类，维护场景执行状态
- [x] 2.7 创建 ScenarioScanner 接口
- [x] 2.8 实现 DirectoryScenarioScanner，扫描 scenarios 目录
- [x] 2.9 创建 ScenarioManager 类，管理已加载的场景配置
- [x] 2.10 添加场景扫描日志记录
- [x] 2.11 创建示例场景配置文件 scenarios/example-scenario/scenario.json

## 3. 场景执行服务

- [x] 3.1 创建 ScenarioService 类
- [x] 3.2 实现 loadScenarios() 方法
- [x] 3.3 实现 listScenarios() 方法返回场景列表
- [x] 3.4 实现 getScenario() 方法返回场景详情
- [x] 3.5 实现 startScenario() 方法，状态转换 IDLE → DEVICE_SELECTION
- [x] 3.6 实现 selectDevices() 方法，锁定所选设备
- [x] 3.7 实现 updateDevices() 方法，支持设备选择阶段修改设备范围
- [x] 3.8 实现 confirmDevices() 方法，确认开始执行，状态转换 DEVICE_SELECTION → RUNNING
- [x] 3.9 实现 getCurrentStep() 方法获取当前步骤信息
- [x] 3.10 实现 executeStep() 方法启动工具执行
- [x] 3.11 扩展 ToolInput 增加 scenario 字段
- [x] 3.12 扩展 ToolInput 增加 previousResult 字段
- [x] 3.13 修改 InputFileGenerator 支持场景模式输入 JSON（通过 ToolInput 扩展自动支持）
- [x] 3.14 在 ToolService 中增加 executeToolInScenario() 方法
- [x] 3.15 实现步骤执行完成处理，推进到下一步骤
- [x] 3.16 实现步骤执行失败处理（根据 allowSkipOnFailure 配置决定失败或跳过）
- [x] 3.17 实现 skipStep() 方法，跳过当前未执行的步骤
- [x] 3.18 实现 terminateScenario() 方法，解锁所有设备
- [x] 3.19 实现 getExecutionStatus() 方法
- [x] 3.20 实现场景完成时自动解锁设备

## 4. 场景 REST API

- [x] 4.1 创建 ScenarioController 类
- [x] 4.2 实现 GET /api/v1/scenarios 场景列表接口
- [x] 4.3 实现 GET /api/v1/scenarios/{id} 场景详情接口
- [x] 4.4 实现 POST /api/v1/scenarios/{id}/start 启动场景接口
- [x] 4.5 实现 POST /api/v1/scenarios/{id}/devices 选择设备接口
- [x] 4.6 实现 PUT /api/v1/scenarios/{id}/devices 修改设备接口
- [x] 4.7 实现 POST /api/v1/scenarios/{id}/confirm 确认开始执行接口
- [x] 4.8 实现 GET /api/v1/scenarios/{id}/status 查询状态接口
- [x] 4.9 实现 POST /api/v1/scenarios/{id}/steps/{index}/execute 执行步骤接口
- [x] 4.10 实现 POST /api/v1/scenarios/{id}/steps/{index}/skip 跳过步骤接口
- [x] 4.11 实现 POST /api/v1/scenarios/{id}/terminate 终止场景接口
- [x] 4.12 创建请求/响应 DTO 类
- [x] 4.13 添加 API 操作日志记录

## 5. 集成与协调

- [x] 5.1 修改 ToolLockManager 集成场景锁定逻辑
- [x] 5.2 确保场景执行时阻止独立工具执行
- [x] 5.3 确保工具执行时阻止场景启动

## 6. 单元测试 - 设备锁定机制

- [ ] 6.1 测试 lockDevices(ips, lockSource) 成功锁定设备
- [ ] 6.2 测试 lockDevices 锁定不存在的设备返回错误
- [ ] 6.3 测试 lockDevices 锁定已被锁定的设备返回错误
- [ ] 6.4 测试 unlockBySource(lockSource) 按来源解锁设备
- [ ] 6.5 测试 getLockInfo(ip) 返回设备锁定信息
- [ ] 6.6 测试更新被锁定的设备返回错误
- [ ] 6.7 测试删除被锁定的设备返回错误
- [ ] 6.8 测试批量删除包含被锁定的设备返回错误
- [ ] 6.9 测试按锁定状态筛选设备列表

## 7. 单元测试 - 场景配置与扫描

- [ ] 7.1 测试解析有效的场景配置文件
- [ ] 7.2 测试解析缺少必需字段的场景配置文件
- [ ] 7.3 测试场景 ID 格式校验（非法字符、超长）
- [ ] 7.4 测试场景扫描器扫描 scenarios 目录
- [ ] 7.5 测试场景管理器加载和查询场景

## 8. 单元测试 - 场景执行服务

- [ ] 8.1 测试场景初始状态为 IDLE
- [ ] 8.2 测试启动场景状态转换为 DEVICE_SELECTION
- [ ] 8.3 测试选择设备后立即锁定设备
- [ ] 8.4 测试选择不存在的设备返回错误
- [ ] 8.5 测试选择已被锁定的设备返回错误
- [ ] 8.6 测试设备选择阶段取消选中设备（解锁并移除）
- [ ] 8.7 测试设备选择阶段新增选中设备（锁定并添加）
- [ ] 8.8 测试第一个子工具启动后禁止修改设备范围
- [ ] 8.9 测试确认开始执行状态转换为 RUNNING
- [ ] 8.10 测试执行步骤启动工具
- [ ] 8.11 测试步骤执行完成状态转换为 completed
- [ ] 8.12 测试步骤执行失败且不允许跳过，场景转换为 FAILED
- [ ] 8.13 测试步骤执行失败且允许跳过，继续下一步骤
- [ ] 8.14 测试允许跳过未执行的步骤
- [ ] 8.15 测试不允许跳过未执行的步骤返回错误
- [ ] 8.16 测试跳过已执行的步骤返回错误
- [ ] 8.17 测试场景全部步骤完成状态转换为 COMPLETED
- [ ] 8.18 测试用户终止场景状态转换为 FAILED
- [ ] 8.19 测试场景完成/失败时解锁设备
- [ ] 8.20 测试场景执行中阻止启动其他场景
- [ ] 8.21 测试工具执行中阻止启动场景
- [ ] 8.22 测试工具输入包含场景上下文信息
- [ ] 8.23 测试工具输入包含前置工具结果

## 9. 集成测试 - REST API

- [ ] 9.1 测试 GET /api/v1/scenarios 返回场景列表
- [ ] 9.2 测试 GET /api/v1/scenarios/{id} 返回场景详情
- [ ] 9.3 测试 GET /api/v1/scenarios/{id} 场景不存在返回 404
- [ ] 9.4 测试 POST /api/v1/scenarios/{id}/start 启动场景
- [ ] 9.5 测试 POST /api/v1/scenarios/{id}/start 场景不存在返回 404
- [ ] 9.6 测试 POST /api/v1/scenarios/{id}/start 场景已被占用返回错误
- [ ] 9.7 测试 POST /api/v1/scenarios/{id}/devices 选择设备
- [ ] 9.8 测试 POST /api/v1/scenarios/{id}/devices 状态无效返回错误
- [ ] 9.9 测试 PUT /api/v1/scenarios/{id}/devices 修改设备范围
- [ ] 9.10 测试 PUT /api/v1/scenarios/{id}/devices 子工具启动后修改返回错误
- [ ] 9.11 测试 POST /api/v1/scenarios/{id}/confirm 确认开始执行
- [ ] 9.12 测试 GET /api/v1/scenarios/{id}/status 查询执行状态
- [ ] 9.13 测试 POST /api/v1/scenarios/{id}/steps/{index}/execute 执行步骤
- [ ] 9.14 测试 POST /api/v1/scenarios/{id}/steps/{index}/skip 跳过步骤
- [ ] 9.15 测试 POST /api/v1/scenarios/{id}/steps/{index}/skip 不允许跳过返回错误
- [ ] 9.16 测试 POST /api/v1/scenarios/{id}/terminate 终止场景
- [ ] 9.17 测试 POST /api/v1/scenarios/{id}/terminate 场景已结束返回错误

## 10. 手动测试

- [ ] 10.1 手动测试完整场景执行流程
- [ ] 10.2 手动测试设备选择阶段修改设备范围功能
- [ ] 10.3 手动测试步骤失败后的跳过功能