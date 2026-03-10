# Scenario Execution

## Purpose

本能力提供场景化执行功能，支持将多个子工具按预定义配置串联执行，实现运维场景的标准化和自动化。场景执行期间自动管理设备锁定、上下文传递和执行记录。

## ADDED Requirements

### Requirement: 场景配置文件格式
系统 SHALL 支持通过 JSON 文件定义场景配置，配置文件位于 `scenarios/<scenario-id>/scenario.json`。

#### Scenario: 解析有效的场景配置文件
- **WHEN** scenarios 目录下存在子目录 "daily-check"，该目录包含有效的 scenario.json
- **THEN** 系统 SHALL 自动加载该场景配置并可供启动

#### Scenario: 场景配置文件缺少必需字段
- **WHEN** scenario.json 缺少 id、name 或 steps 任一必需字段
- **THEN** 系统 SHALL 跳过该场景并记录错误日志

### Requirement: 场景配置数据结构
系统 SHALL 定义场景配置的数据结构，包含以下字段：
- `id`: 场景唯一标识，kebab-case 格式
- `name`: 场景显示名称
- `description`: 场景描述（可选）
- `steps`: 步骤列表，按顺序定义要执行的工具
- `allowSkipOnFailure`: 子工具执行失败后是否允许跳过继续执行（可选，默认 false）
- `allowSkipStep`: 是否允许跳过未执行的子工具（可选，默认 false）

#### Scenario: 场景步骤配置
- **WHEN** 解析场景步骤配置
- **THEN** 每个步骤 SHALL 包含 toolId（工具ID）和可选的 name（步骤名称）

#### Scenario: 场景 ID 格式校验
- **WHEN** 场景 ID 包含非法字符（如空格、@、中文等）或长度超过 64 字符
- **THEN** 系统 SHALL 跳过该场景并记录警告日志

#### Scenario: 默认跳过行为
- **WHEN** 场景配置未指定 allowSkipOnFailure 和 allowSkipStep
- **THEN** 系统 SHALL 使用默认值 false，即不允许跳过

### Requirement: 场景自动发现
系统 SHALL 在应用启动时从 `scenarios` 目录扫描场景配置。

#### Scenario: 扫描到有效的场景
- **WHEN** scenarios 目录下存在包含有效 scenario.json 的子目录
- **THEN** 系统 SHALL 自动加载所有有效场景

#### Scenario: 场景扫描日志
- **WHEN** 应用启动时扫描 scenarios 目录
- **THEN** 系统 SHALL 记录扫描开始、扫描完成、发现场景数量等信息

### Requirement: 场景列表查询
系统 SHALL 提供 REST API 查询可用的场景列表。

#### Scenario: 查询场景列表
- **WHEN** 请求场景列表
- **THEN** 返回所有已加载的场景，包含 id、name、description、steps 数量等信息

#### Scenario: 查询场景详情
- **WHEN** 查询指定场景 ID 的详情
- **THEN** 返回场景完整配置，包含所有步骤信息

#### Scenario: 查询不存在的场景
- **WHEN** 查询不存在的场景 ID
- **THEN** 返回 404 错误 "场景不存在"

### Requirement: 场景执行状态
系统 SHALL 维护场景执行的状态机，包含以下状态：
- `IDLE`: 空闲，场景未启动
- `DEVICE_SELECTION`: 设备选择阶段
- `RUNNING`: 场景执行中
- `COMPLETED`: 场景已完成
- `FAILED`: 场景执行失败

#### Scenario: 场景初始状态
- **WHEN** 场景加载完成
- **THEN** 场景状态为 IDLE

#### Scenario: 启动场景转换状态
- **WHEN** 用户启动场景
- **THEN** 场景状态从 IDLE 转换为 DEVICE_SELECTION

#### Scenario: 选择设备后转换状态
- **WHEN** 用户选择设备并确认开始执行
- **THEN** 场景状态从 DEVICE_SELECTION 转换为 RUNNING

#### Scenario: 步骤失败且不允许跳过转换状态
- **WHEN** 步骤执行失败且场景配置 allowSkipOnFailure 为 false
- **THEN** 场景状态从 RUNNING 转换为 FAILED，解锁所有设备

#### Scenario: 场景完成转换状态
- **WHEN** 场景中所有步骤执行完成或被跳过
- **THEN** 场景状态从 RUNNING 转换为 COMPLETED，解锁所有设备

#### Scenario: 用户终止场景转换状态
- **WHEN** 用户主动终止场景
- **THEN** 场景状态转换为 FAILED，解锁所有设备

### Requirement: 启动场景
系统 SHALL 提供 REST API 启动场景执行。

#### Scenario: 成功启动场景
- **WHEN** 请求启动存在的场景且场景状态为 IDLE
- **THEN** 场景状态转换为 DEVICE_SELECTION，返回场景执行实例 ID

#### Scenario: 启动不存在的场景
- **WHEN** 请求启动不存在的场景
- **THEN** 返回 404 错误 "场景不存在"

#### Scenario: 场景已被占用
- **WHEN** 请求启动场景但该场景已在执行中
- **THEN** 返回错误提示 "场景正在执行中，请等待完成或终止当前执行"

### Requirement: 场景设备选择
系统 SHALL 提供 REST API 在场景执行中选择待维护的设备。

#### Scenario: 成功选择设备
- **WHEN** 场景状态为 DEVICE_SELECTION，提交有效的设备 IP 列表
- **THEN** 锁定所选设备，场景状态保持 DEVICE_SELECTION，返回当前步骤信息

#### Scenario: 选择设备后立即锁定
- **WHEN** 用户选择设备
- **THEN** 系统 SHALL 立即锁定所选设备

#### Scenario: 选择设备时场景状态无效
- **WHEN** 场景状态不是 DEVICE_SELECTION 时尝试选择设备
- **THEN** 返回错误提示 "当前场景状态不允许选择设备"

#### Scenario: 选择不存在的设备
- **WHEN** 提交的设备 IP 列表中包含不存在的设备
- **THEN** 返回错误提示，说明哪些设备不存在

#### Scenario: 选择已被锁定的设备
- **WHEN** 提交的设备 IP 列表中包含已被其他场景锁定的设备
- **THEN** 返回错误提示 "设备已被锁定"，说明哪些设备被锁定

### Requirement: 设备选择阶段修改设备范围
系统 SHALL 允许在第一个子工具启动前修改已选中的设备范围。

#### Scenario: 取消选中已选设备
- **WHEN** 场景状态为 DEVICE_SELECTION，用户取消选中某个已锁定的设备
- **THEN** 系统 SHALL 解锁该设备并从选中列表中移除

#### Scenario: 新增选中设备
- **WHEN** 场景状态为 DEVICE_SELECTION，用户新选中其他设备
- **THEN** 系统 SHALL 锁定新选中的设备并添加到选中列表

#### Scenario: 第一个子工具启动后禁止修改设备
- **WHEN** 场景中至少有一个子工具已执行或正在执行
- **THEN** 系统 SHALL 拒绝修改设备范围的请求，返回错误提示 "场景已开始执行，无法修改设备"

#### Scenario: 场景未开始时允许修改设备
- **WHEN** 场景状态为 DEVICE_SELECTION 且尚未执行任何子工具
- **THEN** 系统 SHALL 允许用户修改设备范围

### Requirement: 场景步骤执行
系统 SHALL 支持用户逐个启动场景中的工具步骤，子工具运行完毕后不会自动运行下一个子工具。

#### Scenario: 获取当前步骤信息
- **WHEN** 场景状态为 RUNNING，查询当前步骤
- **THEN** 返回当前步骤索引、工具 ID、步骤名称、执行状态（pending/running/completed/failed/skipped）

#### Scenario: 启动当前步骤
- **WHEN** 场景状态为 RUNNING，当前步骤状态为 pending，请求启动步骤
- **THEN** 启动对应工具，当前步骤状态转换为 running

#### Scenario: 步骤执行完成
- **WHEN** 工具执行完成且结果为 success
- **THEN** 当前步骤状态转换为 completed，场景推进到下一步骤

#### Scenario: 步骤执行失败且不允许跳过
- **WHEN** 工具执行失败且场景配置 allowSkipOnFailure 为 false
- **THEN** 当前步骤状态转换为 failed，场景状态转换为 FAILED，解锁所有设备

#### Scenario: 步骤执行失败且允许跳过
- **WHEN** 工具执行失败且场景配置 allowSkipOnFailure 为 true
- **THEN** 当前步骤状态转换为 failed，场景推进到下一步骤

#### Scenario: 场景全部步骤完成
- **WHEN** 最后一个步骤执行完成或被跳过
- **THEN** 场景状态转换为 COMPLETED，解锁所有设备

#### Scenario: 步骤执行完毕不自动执行下一步
- **WHEN** 子工具执行完毕
- **THEN** 系统 SHALL 等待用户手动启动下一个子工具

### Requirement: 跳过未执行的子工具
系统 SHALL 根据场景配置决定是否允许跳过未执行的子工具。

#### Scenario: 允许跳过未执行的步骤
- **WHEN** 场景配置 allowSkipStep 为 true，用户请求跳过当前未执行的步骤
- **THEN** 当前步骤状态转换为 skipped，场景推进到下一步骤

#### Scenario: 不允许跳过未执行的步骤
- **WHEN** 场景配置 allowSkipStep 为 false，用户请求跳过当前未执行的步骤
- **THEN** 返回错误提示 "当前场景不允许跳过子工具"

#### Scenario: 跳过已执行或正在执行的步骤
- **WHEN** 用户请求跳过已执行或正在执行的步骤
- **THEN** 返回错误提示 "只能跳过未执行的步骤"

### Requirement: 场景上下文传递
系统 SHALL 在每个工具执行时传递场景上下文信息。

#### Scenario: 传递设备信息
- **WHEN** 启动场景中的工具
- **THEN** 工具输入 JSON SHALL 包含用户选择的设备列表

#### Scenario: 传递场景信息
- **WHEN** 启动场景中的工具
- **THEN** 工具输入 JSON SHALL 包含场景 ID、场景名称、当前步骤索引

#### Scenario: 传递前置工具结果
- **WHEN** 启动非第一个步骤的工具
- **THEN** 工具输入 JSON SHALL 包含前置工具的执行结果（包含 deviceResults 和 artifacts）

#### Scenario: 第一个步骤无前置结果
- **WHEN** 启动场景第一个步骤的工具
- **THEN** 工具输入 JSON 不包含前置工具结果字段

### Requirement: 场景执行期间设备锁定
系统 SHALL 在场景执行期间锁定所选设备，禁止变更操作。

#### Scenario: 场景锁定设备
- **WHEN** 用户在场景中选择设备
- **THEN** 系统 SHALL 调用设备管理模块锁定所选设备，锁定原因为场景 ID

#### Scenario: 场景执行期间尝试修改设备
- **WHEN** 场景正在执行，用户尝试修改被锁定的设备
- **THEN** 设备管理模块 SHALL 拒绝变更，返回 "设备正在被场景 [场景名称] 使用，无法修改"

#### Scenario: 场景执行期间尝试删除设备
- **WHEN** 场景正在执行，用户尝试删除被锁定的设备
- **THEN** 设备管理模块 SHALL 拒绝删除，返回 "设备正在被场景 [场景名称] 使用，无法删除"

#### Scenario: 子工具执行失败不释放锁
- **WHEN** 场景中某个子工具执行失败
- **THEN** 系统 SHALL 保持设备锁定状态，等待用户决策

#### Scenario: 用户退出场景释放设备锁
- **WHEN** 用户主动退出或终止场景
- **THEN** 系统 SHALL 解锁所有被该场景锁定的设备

#### Scenario: 场景完成释放设备锁
- **WHEN** 场景中所有子工具执行完成
- **THEN** 系统 SHALL 解锁所有被该场景锁定的设备

### Requirement: 场景终止
系统 SHALL 提供 REST API 终止正在执行的场景。

#### Scenario: 成功终止场景
- **WHEN** 场景状态为 RUNNING 或 PAUSED 或 DEVICE_SELECTION，请求终止场景
- **THEN** 终止当前正在执行的工具（如有），场景状态转换为 FAILED，解锁所有设备

#### Scenario: 终止不存在的场景执行
- **WHEN** 请求终止不存在的场景执行实例
- **THEN** 返回 404 错误 "场景执行实例不存在"

#### Scenario: 终止已完成的场景
- **WHEN** 场景状态为 COMPLETED 或 FAILED 时请求终止
- **THEN** 返回错误提示 "场景已结束，无法终止"

### Requirement: 场景执行查询
系统 SHALL 提供 REST API 查询场景执行状态和结果。

#### Scenario: 查询场景执行状态
- **WHEN** 查询场景执行实例
- **THEN** 返回场景状态、当前步骤、已完成步骤列表、各步骤执行结果摘要

#### Scenario: 查询场景步骤详情
- **WHEN** 查询场景中某个步骤的详情
- **THEN** 返回该步骤的工具 ID、执行状态、开始时间、结束时间、设备结果、交付件列表

### Requirement: 场景执行记录
系统 SHALL 记录场景执行的历史记录。

#### Scenario: 记录场景执行开始
- **WHEN** 场景状态从 IDLE 转换为 DEVICE_SELECTION
- **THEN** 系统 SHALL 记录场景 ID、启动时间、操作者信息

#### Scenario: 记录场景步骤执行
- **WHEN** 场景中某个步骤开始执行
- **THEN** 系统 SHALL 记录步骤索引、工具 ID、开始时间

#### Scenario: 记录场景步骤完成
- **WHEN** 场景中某个步骤执行完成
- **THEN** 系统 SHALL 记录步骤索引、结束时间、执行结果、设备结果摘要

#### Scenario: 记录场景执行结束
- **WHEN** 场景状态转换为 COMPLETED 或 FAILED
- **THEN** 系统 SHALL 记录场景 ID、结束时间、最终状态、总耗时

### Requirement: 场景执行约束
系统 SHALL 强制执行场景化执行的约束条件。

#### Scenario: 仅支持串行执行
- **WHEN** 场景配置包含多个步骤
- **THEN** 系统 SHALL 按顺序逐个执行步骤，不支持并行执行

#### Scenario: 不支持分支执行
- **WHEN** 场景执行过程中
- **THEN** 系统 SHALL 严格按照配置的步骤顺序执行，不支持条件分支

#### Scenario: 不支持异步执行
- **WHEN** 用户启动场景中的工具步骤
- **THEN** 系统 SHALL 等待工具执行完成后再允许下一步操作

### Requirement: 单场景执行限制
系统 SHALL 限制同一时间只能执行一个场景实例。

#### Scenario: 场景执行中禁止启动其他场景
- **WHEN** 存在场景正在执行中
- **THEN** 系统 SHALL 拒绝启动其他场景，返回错误提示

#### Scenario: 同一场景重复启动
- **WHEN** 尝试启动已在执行中的场景
- **THEN** 系统 SHALL 返回错误提示 "场景正在执行中"

### Requirement: 场景 API 版本控制
系统 SHALL 对所有场景 API 使用版本控制前缀 `/api/v1`。

#### Scenario: 使用版本化 API
- **WHEN** 访问场景 API
- **THEN** URL 路径以 `/api/v1/scenarios` 开头

### Requirement: 场景操作日志
系统 SHALL 记录场景模块的关键操作日志。

#### Scenario: 场景启动日志
- **WHEN** 成功启动场景
- **THEN** 系统 SHALL 记录场景 ID、场景名称、操作者

#### Scenario: 场景终止日志
- **WHEN** 终止场景执行
- **THEN** 系统 SHALL 记录场景 ID、终止原因、操作者

#### Scenario: 场景完成日志
- **WHEN** 场景执行完成
- **THEN** 系统 SHALL 记录场景 ID、最终状态、总耗时、处理设备数量