# tool Specification

## Purpose
TBD - created by archiving change add-subtool-module. Update Purpose after archive.
## Requirements
### Requirement: 工具自动发现
应用启动时 SHALL 从 `tools` 目录扫描一级子目录，每个子目录名作为工具的 ID。

#### Scenario: 扫描到有效的工具目录
- **WHEN** tools 目录下存在子目录 "device-ping"，该目录包含有效的 tool.json
- **THEN** 系统 SHALL 自动加载该工具并可供调用

#### Scenario: 目录名称不符合 ID 规范
- **WHEN** 存在子目录名称包含非法字符（如空格、@、中文等）或长度超过64字符
- **THEN** 系统 SHALL 跳过该目录并记录警告日志

#### Scenario: 工具目录缺少配置文件
- **WHEN** 存在子目录但目录下没有 tool.json 文件
- **THEN** 系统 SHALL 跳过该目录并记录警告日志

### Requirement: tool.json 配置格式
工具目录下 SHALL 包含 tool.json 文件，定义工具的元数据和启动方式。

#### Scenario: 解析有效的 tool.json（字符串命令）
- **WHEN** tool.json 包含 id、name、version、command（字符串类型）字段
- **THEN** 系统 SHALL 成功加载工具配置

#### Scenario: 解析有效的 tool.json（对象命令）
- **WHEN** tool.json 包含 id、name、version、command（对象类型）字段，包含 windows 和/或 linux 键
- **THEN** 系统 SHALL 根据当前平台选择对应的命令

#### Scenario: tool.json 缺少必需字段
- **WHEN** tool.json 缺少 id、name、version 或 command 任一必需字段
- **THEN** 系统 SHALL 跳过该工具并记录错误日志

### Requirement: 平台区分的启动命令
系统 SHALL 根据运行平台使用不同的可执行文件。

#### Scenario: Windows 平台使用 Windows 命令
- **WHEN** 在 Windows 系统上运行，tool.json 中 command.windows 为 "ping.bat"
- **THEN** 系统 SHALL 执行 ping.bat

#### Scenario: Linux 平台使用 Linux 命令
- **WHEN** 在 Linux 系统上运行，tool.json 中 command.linux 为 "ping.sh"
- **THEN** 系统 SHALL 执行 ping.sh

#### Scenario: 跨平台共用同一命令
- **WHEN** tool.json 中 command 为字符串 "script.bat"，在 Linux 上运行
- **THEN** 系统 SHALL 尝试执行 script.bat（可能失败，由工具自身处理）

### Requirement: 工具调用
系统 SHALL 通过命令行参数传递工作目录，工具在该目录下进行输入输出操作。

#### Scenario: 启动工具传递工作目录
- **WHEN** 调用工具，设备列表包含 ip "192.168.1.1" 和 "192.168.1.2"
- **THEN** 系统 SHALL 创建工作目录 `data/archive/device-ping-20260303233401123/`，并通过 `--work-dir` 参数传递给工具

#### Scenario: 输入 JSON 包含完整设备信息
- **WHEN** 构建输入 JSON
- **THEN** JSON SHALL 包含 devices 数组，每个设备包含 ip、name、port、username、password（明文）

#### Scenario: 工具长时间运行
- **WHEN** 工具运行时间超过预期
- **THEN** 系统 SHALL 等待工具运行完成，不设超时限制

### Requirement: 工具执行监控
系统 SHALL 通过轮询 result.json 中的 status 字段检测工具执行完毕。

#### Scenario: 检测工具执行完毕
- **WHEN** 工具正在运行，result.json 中 status 字段为 "running"
- **THEN** 系统 SHALL 每秒轮询一次 until status 变为 "completed"

#### Scenario: 工具执行完成
- **WHEN** 工具执行完毕，result.json 中 status 变为 "completed"，result 为 "success" 或 "failed"
- **THEN** 系统 SHALL 读取结果并返回给调用方

### Requirement: 运维结果存储
系统 SHALL 将工具运行结果保存到工作目录中。

#### Scenario: 结果保存到工作目录（首次运行）
- **WHEN** 工具首次执行，启动时间为 2026-03-03 23:34:01.123
- **THEN** 系统 SHALL 在 `data/archive/device-ping-20260303233401123/` 目录创建 result.json 文件

#### Scenario: 结果保存到工作目录（目录重复）
- **WHEN** 工具执行，但目录 `device-ping-20260303233401123/` 已存在
- **THEN** 系统 SHALL 使用 `device-ping-20260303233401123-1/` 目录，如仍存在则继续递增

#### Scenario: 交付件保存到工作目录
- **WHEN** 工具在运行过程中生成交付件（如报告文件）
- **THEN** 交付件 SHALL 由工具直接保存到工作目录，交付件信息记录在 result.json 的 artifacts 字段中

#### Scenario: 结果 JSON 包含设备运维结果
- **WHEN** 解析工具返回的结果 JSON
- **THEN** 结果 JSON SHALL 包含 deviceResults 数组，每个元素包含 ip、status（success/failed/not_executed）和可选的 message

#### Scenario: 结果 JSON 包含执行结果
- **WHEN** 解析工具返回的结果 JSON
- **THEN** 结果 JSON SHALL 包含 status（值为 "completed"）和 result（值为 "success" 或 "failed"）字段

#### Scenario: 结果 JSON 时间格式
- **WHEN** 构建结果 JSON 中的时间字段
- **THEN** 时间字段 SHALL 格式化为 "2026-03-03 23:34:01.123"

#### Scenario: 结果 JSON 包含交付件信息
- **WHEN** 工具生成了交付件
- **THEN** 结果 JSON SHALL 包含 artifacts 数组，记录每个交付件的 name、path 和 size

### Requirement: 运行锁机制
系统 SHALL 在工具运行期间锁定相关资源，防止并发操作。

#### Scenario: 工具运行期间尝试运行另一个工具
- **WHEN** 工具 A 正在运行，尝试启动工具 B
- **THEN** 系统 SHALL 拒绝启动工具 B，并返回工具运行中错误

#### Scenario: 工具运行前锁定设备
- **WHEN** 工具准备运行，选中设备 A 和 B
- **THEN** 系统 SHALL 在启动工具前调用设备管理模块锁定设备 A 和 B

#### Scenario: 工具运行期间尝试变更设备
- **WHEN** 工具正在运维设备 A 和设备 B，用户尝试修改设备 A 的信息
- **THEN** 设备管理模块 SHALL 拒绝变更，并返回设备被锁定错误

#### Scenario: 工具运行期间尝试删除设备
- **WHEN** 工具正在运维设备 A 和设备 B，用户尝试删除设备 A
- **THEN** 设备管理模块 SHALL 拒绝删除，并返回设备被锁定错误

#### Scenario: 工具运行完成释放锁
- **WHEN** 工具运行完成（result 为 "success" 或 "failed"）
- **THEN** 系统 SHALL 调用设备管理模块解锁所有设备，其他操作可以正常进行

### Requirement: 操作日志
系统 SHALL 记录工具模块的关键操作日志。

#### Scenario: 工具扫描日志
- **WHEN** 应用启动时扫描 tools 目录
- **THEN** 系统 SHALL 记录扫描开始、扫描完成、发现工具数量等信息

#### Scenario: 工具扫描跳过无效目录日志
- **WHEN** 扫描时遇到不符合规范的目录（ID不合法、缺少配置文件）
- **THEN** 系统 SHALL 记录跳过原因

#### Scenario: 工具启动日志
- **WHEN** 成功启动工具
- **THEN** 系统 SHALL 记录工具ID、选中设备列表、工作目录、启动时间

#### Scenario: 工具结束日志
- **WHEN** 工具执行完成
- **THEN** 系统 SHALL 记录工具ID、执行结果（success/failed）、结束时间、耗时

### Requirement: 场景上下文输入
系统 SHALL 支持在工具输入 JSON 中包含场景上下文信息。

#### Scenario: 场景模式下工具输入包含场景信息
- **WHEN** 在场景执行中启动工具
- **THEN** 工具输入 JSON SHALL 包含 scenario 对象，包含 scenarioId、scenarioName、stepIndex 字段

#### Scenario: 场景模式下工具输入包含设备列表
- **WHEN** 在场景执行中启动工具
- **THEN** 工具输入 JSON 的 devices 数组 SHALL 包含用户在场景中选择的设备列表

#### Scenario: 独立执行模式无场景信息
- **WHEN** 独立启动工具（非场景模式）
- **THEN** 工具输入 JSON 不包含 scenario 对象

### Requirement: 前置工具结果传递
系统 SHALL 支持将前置工具的执行结果传递给后续工具。

#### Scenario: 传递前置工具结果
- **WHEN** 在场景执行中启动非第一个步骤的工具
- **THEN** 工具输入 JSON SHALL 包含 previousResult 对象，包含前置工具的 deviceResults 和 artifacts

#### Scenario: 前置工具结果格式
- **WHEN** 传递前置工具结果
- **THEN** previousResult 对象 SHALL 包含 status、result、deviceResults、artifacts 字段

#### Scenario: 第一个步骤无前置结果
- **WHEN** 在场景执行中启动第一个步骤的工具
- **THEN** 工具输入 JSON 不包含 previousResult 对象

### Requirement: 工具执行模式标识
系统 SHALL 在工具调用时标识执行模式（独立执行/场景执行）。

#### Scenario: 场景执行模式
- **WHEN** 在场景中启动工具
- **THEN** 工作目录命名格式为 `data/archive/<scenario-id>/<tool-id>-<timestamp>/`

#### Scenario: 独立执行模式
- **WHEN** 独立启动工具
- **THEN** 工作目录命名格式为 `data/archive/<tool-id>-<timestamp>/`

### Requirement: 工具输入 JSON 完整结构
系统 SHALL 定义工具输入 JSON 的完整结构。

#### Scenario: 完整输入 JSON 结构（场景模式）
- **WHEN** 在场景执行中启动工具
- **THEN** 输入 JSON SHALL 包含以下结构：
  - `devices`: 设备数组
  - `scenario`: 场景信息（scenarioId、scenarioName、stepIndex）
  - `previousResult`: 前置工具结果（非第一步时）
  - `executionTime`: 执行时间

#### Scenario: 完整输入 JSON 结构（独立模式）
- **WHEN** 独立启动工具
- **THEN** 输入 JSON SHALL 包含以下结构：
  - `devices`: 设备数组
  - `executionTime`: 执行时间

### Requirement: 工具场景关联日志
系统 SHALL 记录工具与场景的关联信息。

#### Scenario: 场景模式工具启动日志
- **WHEN** 在场景中启动工具
- **THEN** 日志 SHALL 记录场景 ID、步骤索引、工具 ID

#### Scenario: 场景模式工具结束日志
- **WHEN** 场景中的工具执行完成
- **THEN** 日志 SHALL 记录场景 ID、步骤索引、工具 ID、执行结果

