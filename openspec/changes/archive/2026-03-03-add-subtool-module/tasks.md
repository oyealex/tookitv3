## 1. 配置模型

- [x] 1.1 创建 ToolConfig 实体类（从 tool.json 加载）
- [x] 1.2 创建设备信息 DeviceInfo DTO（ip、name、port、username、password）
- [x] 1.3 创建工具输入数据 ToolInput DTO（toolId、toolName、startTime、workDir、devices）
- [x] 1.4 创建设备运维结果 DeviceResult DTO（ip、status、message）
- [x] 1.5 创建交付件信息 Artifact DTO（name、path、size）
- [x] 1.6 创建工具结果 ToolResult DTO（deviceResults、artifacts、status、result、startTime、endTime）

## 2. 工具扫描

- [x] 2.1 创建 ToolScanner 接口
- [x] 2.2 实现 DirectoryToolScanner（扫描 tools 目录）
- [x] 2.3 实现 tool.json 解析器（使用 Jackson）
- [x] 2.4 实现 ID 验证工具类（正则 [a-zA-Z0-9_-]{1,64}）
- [x] 2.5 创建工具管理器 ToolManager（管理已发现的工具）
- [x] 2.6 实现工具扫描操作日志（扫描开始/完成/跳过原因）

## 3. 工具调用

- [x] 3.1 创建 ToolInvoker 接口
- [x] 3.2 实现 ProcessToolInvoker（进程调用器）
- [x] 3.3 实现工作目录管理类（创建 data/archive/{toolid}-{time} 目录，重复序号处理）
- [x] 3.4 实现输入文件生成器（生成 input.json 到工作目录）
- [x] 3.5 实现平台判断工具类（Windows/Linux）
- [x] 3.6 实现时间格式化工具（目录名格式：20260303233450123，JSON格式：2026-03-03 23:34:01.123）

## 4. 工具执行监控

- [x] 4.1 创建 ToolExecutionMonitor 监控器接口
- [x] 4.2 实现 PollingToolExecutionMonitor（轮询 result.json）
- [x] 4.3 实现结果解析器（读取并解析 result.json）
- [x] 4.4 实现执行完成检测逻辑（status = "completed"）

## 5. 运行锁机制

- [x] 5.1 创建 ToolLockManager 锁管理器
- [x] 5.2 实现全局运行锁（同一时间只能运行一个工具）
- [x] 5.3 实现设备锁调用接口（依赖设备管理模块）
- [x] 5.4 实现锁的获取和释放逻辑（调用 Device 模块接口）

## 6. 服务层

- [x] 6.1 创建 ToolService 服务类
- [x] 6.2 实现工具查询功能（getTool、listTools）
- [x] 6.3 实现工具调用功能（executeTool）
- [x] 6.4 实现工具启动操作日志
- [x] 6.5 实现工具结束操作日志
- [x] 6.6 实现启动时自动扫描
- [x] 6.7 实现运维历史查询功能

## 7. 模块内 UT 测试

- [x] 7.1 编写 ToolConfigTest（JSON 解析）
- [x] 7.2 编写 ToolScannerTest（目录扫描）
- [x] 7.3 编写 ToolInvokerTest（进程调用）
- [x] 7.4 编写 WorkDirectoryManagerTest（工作目录创建、序号处理）
- [x] 7.5 编写 ToolLockManagerTest（全局锁）
- [x] 7.6 编写 DateTimeUtilTest（时间格式化）
- [x] 7.7 编写 ToolExecutionMonitorTest（结果监控）

## 8. 模块级集成测试

- [x] 8.1 编写 ToolScannerIntegrationTest（扫描 + 解析完整流程）
- [x] 8.2 编写 ToolManagerTest（工具管理，包含完整流程）
- [x] 8.3 编写 ToolLockManagerTest（并发锁测试）
- [x] 8.4 编写 WorkDirectoryManagerTest（历史记录测试）