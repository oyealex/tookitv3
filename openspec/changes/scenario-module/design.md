## Context

当前系统已具备以下能力：
- **设备管理**：支持设备的增删改查，`DeviceLockService` 接口已定义但尚未实现
- **工具模块**：支持单个工具的独立执行，包含 `ToolLockManager` 用于全局锁管理
- **锁定机制**：`DeviceLockService` 接口已定义 `lockDevices()`、`unlockDevices()`、`isLocked()` 方法；`ToolLockManager` 已实现全局锁和设备锁协调；`tool` spec 已定义设备锁定需求

**已有实现**：
- `DeviceLockService` 接口：定义了锁定/解锁/检查方法
- `ToolLockManager`：实现了全局锁管理，调用 `DeviceLockService` 进行设备锁定
- `tool` spec：已定义工具运行期间设备锁定保护的规范

**待实现**：
- `DeviceLockService` 接口实现（在 `DeviceServiceImpl` 中）
- 设备变更操作的锁定检查
- 锁定来源支持（用于场景维度管理）

场景化模块需要在现有基础上扩展：
1. 实现 `DeviceLockService` 接口并添加锁定来源支持
2. 场景配置管理与自动发现
3. 场景执行状态机管理
4. 工具输入扩展（场景上下文和前置结果传递）

## Goals / Non-Goals

**Goals:**
- 实现场景配置文件扫描和加载
- 实现场景执行状态机（IDLE → DEVICE_SELECTION → RUNNING → COMPLETED/FAILED）
- 扩展设备管理模块，实现设备锁定/解锁机制
- 扩展工具调用，支持场景上下文和前置工具结果传递
- 实现场景执行 REST API

**Non-Goals:**
- 不支持并行、异步或分支执行
- 不支持场景配置的热更新（需重启应用）
- 不支持场景执行的历史记录持久化（本次仅内存记录）
- 不支持场景模板或场景克隆

## Decisions

### Decision 1: 场景配置存储方式

**选择**: JSON 文件存储在 `scenarios/` 目录

**备选方案**:
- A) JSON 文件存储（选中）
- B) 数据库存储
- C) YAML 文件存储

**理由**:
- 与现有 `tools/` 目录的工具配置方式一致
- 便于版本控制和配置迁移
- 简化实现，无需新建数据库表

### Decision 2: 设备锁定实现方式

**选择**: 在 `DeviceServiceImpl` 中实现 `DeviceLockService` 接口，扩展支持锁定来源

**现有基础**:
- `DeviceLockService` 接口已定义 `lockDevices()`、`unlockDevices()`、`isLocked()` 方法
- `ToolLockManager` 已实现调用逻辑

**需要扩展**:
- `DeviceServiceImpl` 实现 `DeviceLockService` 接口
- 扩展接口支持锁定来源参数 `lockDevices(ips, lockSource)`
- 新增 `unlockBySource(lockSource)` 方法按来源解锁
- 新增 `getLockInfo(ip)` 方法获取锁定详情

**理由**:
- 设备锁定是设备管理模块的职责，应在 DeviceServiceImpl 中实现
- 使用内存 Map 存储锁定状态（含锁定来源），避免修改数据库结构
- 与现有 `ToolLockManager` 协调机制保持一致

### Decision 3: 场景执行状态管理

**选择**: 单例模式 + 内存状态

**备选方案**:
- A) 单例模式 + 内存状态（选中）
- B) 数据库持久化状态
- C) 分布式状态管理

**理由**:
- 系统限制同一时间只能执行一个场景实例
- 内存管理简单高效，无需数据库交互
- 应用重启后状态自然重置，符合预期

### Decision 4: 场景与工具执行的协调

**选择**: 场景模块调用 ToolService，通过参数区分执行模式

**备选方案**:
- A) 场景模块调用 ToolService（选中）
- B) 创建独立的 ScenarioToolInvoker
- C) 修改 ToolService 支持场景模式

**理由**:
- 复用现有工具执行逻辑
- 通过扩展 ToolInput 结构传递场景上下文
- 保持工具模块的独立性

### Decision 5: 前置工具结果传递方式

**选择**: 在 ToolInput 中增加 `previousResult` 字段

**备选方案**:
- A) 在 ToolInput 中增加 previousResult 字段（选中）
- B) 工具主动读取工作目录
- C) 通过命令行参数传递

**理由**:
- 统一在输入 JSON 中传递所有上下文信息
- 工具实现简单，无需关心数据来源
- 保持现有工具接口的向后兼容

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Controller Layer                        │
├─────────────────────────────────────────────────────────────────┤
│  ScenarioController                                              │
│  - GET  /api/v1/scenarios          # 场景列表                    │
│  - GET  /api/v1/scenarios/{id}     # 场景详情                    │
│  - POST /api/v1/scenarios/{id}/start    # 启动场景              │
│  - POST /api/v1/scenarios/{id}/devices  # 选择设备              │
│  - PUT  /api/v1/scenarios/{id}/devices  # 修改设备范围          │
│  - POST /api/v1/scenarios/{id}/confirm  # 确认开始执行          │
│  - GET  /api/v1/scenarios/{id}/status   # 查询状态              │
│  - POST /api/v1/scenarios/{id}/steps/{index}/execute # 执行步骤 │
│  - POST /api/v1/scenarios/{id}/steps/{index}/skip     # 跳过步骤│
│  - POST /api/v1/scenarios/{id}/terminate # 终止场景             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                          Service Layer                           │
├─────────────────────────────────────────────────────────────────┤
│  ScenarioService                                                 │
│  - loadScenarios()        # 加载场景配置                         │
│  - startScenario()        # 启动场景                             │
│  - selectDevices()        # 选择设备                             │
│  - updateDevices()        # 修改设备范围                         │
│  - confirmDevices()       # 确认开始执行                         │
│  - executeStep()          # 执行步骤                             │
│  - skipStep()             # 跳过步骤                             │
│  - terminateScenario()    # 终止场景                             │
│                                                                  │
│  DeviceServiceImpl (修改)                                        │
│  - lockDevices()          # 锁定设备                             │
│  - unlockDevices()        # 解锁设备                             │
│  - unlockBySource()       # 按来源解锁                           │
│  - isLocked()             # 检查锁定状态                         │
│  - getLockInfo()          # 获取锁定信息                         │
│                                                                  │
│  ToolService (修改)                                              │
│  - executeToolInScenario() # 场景模式执行工具                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Model Layer                              │
├─────────────────────────────────────────────────────────────────┤
│  ScenarioConfig          # 场景配置                              │
│  ScenarioStep            # 场景步骤                              │
│  ScenarioExecution       # 场景执行实例                          │
│  ScenarioStepExecution   # 步骤执行记录                          │
│  ScenarioStatus (enum)   # 场景状态枚举                          │
│  DeviceLockInfo          # 设备锁定信息                          │
└─────────────────────────────────────────────────────────────────┘
```

## Key Classes

### 新增类

| 类名 | 职责 |
|------|------|
| `ScenarioConfig` | 场景配置模型，对应 scenario.json |
| `ScenarioStep` | 场景步骤模型 |
| `ScenarioScanner` | 场景扫描器，启动时扫描 scenarios 目录 |
| `ScenarioManager` | 场景管理器，管理已加载的场景配置 |
| `ScenarioService` | 场景服务，处理场景执行逻辑 |
| `ScenarioExecution` | 场景执行实例，维护执行状态 |
| `ScenarioStepExecution` | 步骤执行记录 |
| `ScenarioController` | 场景 REST API 控制器 |
| `DeviceLockInfo` | 设备锁定信息模型 |

### 修改类

| 类名 | 修改内容 |
|------|----------|
| `DeviceLockService` | 扩展接口，增加锁定来源参数 |
| `DeviceServiceImpl` | 实现 DeviceLockService |
| `DeviceEntity` | 无需修改（锁定状态使用内存管理） |
| `ToolInput` | 增加 scenario 和 previousResult 字段 |
| `InputFileGenerator` | 支持生成场景模式输入 JSON |
| `ToolService` | 增加场景模式执行方法 |

## Risks / Trade-offs

| 风险 | 缓解措施 |
|------|----------|
| 应用重启丢失场景执行状态 | 设计文档明确此为预期行为；未来可扩展持久化 |
| 设备锁定状态仅内存存储 | 使用 ConcurrentHashMap 保证线程安全；重启后自动释放 |
| 场景执行阻塞工具独立执行 | 复用 ToolLockManager 的全局锁机制，统一协调 |
| 前置结果过大导致内存问题 | previousResult 仅包含必要字段（deviceResults、artifacts 路径） |

## Migration Plan

1. **阶段 1**：实现设备锁定机制
   - 扩展 DeviceLockService 接口
   - 在 DeviceServiceImpl 中实现锁定逻辑
   - 修改设备变更操作检查锁定状态

2. **阶段 2**：实现场景配置管理
   - 创建场景配置模型
   - 实现场景扫描器
   - 实现场景管理器

3. **阶段 3**：实现场景执行服务
   - 实现场景执行状态机
   - 扩展 ToolInput 和 ToolService
   - 实现场景步骤执行逻辑

4. **阶段 4**：实现 REST API
   - 创建 ScenarioController
   - 实现场景管理 API 端点

## Open Questions

1. ~~**场景执行失败后的设备解锁时机**~~
   - **决策**：设备在选择后立即锁定，仅在用户主动退出/终止场景或场景全部完成时解锁。子工具执行失败不释放锁。

2. ~~**工具执行失败是否允许跳过继续执行后续步骤**~~
   - **决策**：由场景配置 `allowSkipOnFailure` 决定，默认 false。若允许，失败后自动跳过继续执行；若不允许，场景直接失败结束。

3. ~~**场景配置中的步骤是否需要支持可选步骤**~~
   - **决策**：由场景配置 `allowSkipStep` 决定是否允许跳过未执行的子工具，默认 false。

4. **设备选择阶段的行为**：
   - 选择设备后立即锁定
   - 第一个子工具启动前允许修改设备范围（取消选中则解锁，新增选中则锁定）
   - 第一个子工具启动后禁止修改设备范围

5. **场景执行模式**：
   - 场景不是自驱动的，由用户选择何时执行子工具
   - 子工具运行完毕后不会自动运行下一个子工具
   - 不需要暂停功能