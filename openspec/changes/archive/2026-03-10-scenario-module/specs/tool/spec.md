# Tool (Delta Spec)

## Purpose

扩展现有工具能力，支持在场景执行上下文中调用工具，传递场景信息和前置工具执行结果。

## ADDED Requirements

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