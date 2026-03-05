---
name: "OPSX: 继续"
description: "继续处理变更 - 创建下一个制品（实验性）"
category: Workflow
tags: [workflow, artifacts, experimental]
---

通过创建下一个制品来继续处理变更。

**输入**：可选择在 `/opsx:continue` 后指定变更名称（例如 `/opsx:continue add-auth`）。如果省略，检查是否可以从对话上下文中推断。如果模糊或不确定，你必须提示选择可用变更。

**步骤**

1. **如果未提供变更名称，提示选择**

   运行 `openspec list --json` 获取按最近修改排序的可用变更。然后使用 **AskUserQuestion 工具** 让用户选择要处理的变更。

   将最近修改的 3-4 个变更作为选项展示，显示：
   - 变更名称
   - 模式（如果存在，来自 `schema` 字段，否则为 "spec-driven"）
   - 状态（例如 "0/5 tasks"、"complete"、"no tasks"）
   - 最近修改时间（来自 `lastModified` 字段）

   将最近修改的变更标记为"（推荐）"，因为它很可能是用户想要继续的。

   **重要**：不要猜测或自动选择变更。始终让用户选择。

2. **检查当前状态**
   ```bash
   openspec status --change "<name>" --json
   ```
   解析 JSON 以了解当前状态。响应包括：
   - `schemaName`：使用的工作流程模式（例如 "spec-driven"）
   - `artifacts`：制品及其状态（"done"、"ready"、"blocked"）的数组
   - `isComplete`：布尔值，指示所有制品是否完成

3. **根据状态行动**：

   ---

   **如果所有制品都完成（`isComplete: true`）**：
   - 祝贺用户
   - 显示包括使用模式在内的最终状态
   - 建议："所有制品已创建！现在可以使用 `/opsx:apply` 实现此变更或使用 `/opsx:archive` 归档。"
   - 停止

   ---

   **如果制品已准备好创建**（状态显示带有 `status: "ready"` 的制品）：
   - 从状态输出中选择第一个带有 `status: "ready"` 的制品
   - 获取其指令：
     ```bash
     openspec instructions <artifact-id> --change "<name>" --json
     ```
   - 解析 JSON。关键字段是：
     - `context`：项目背景（对你的约束——不要包含在输出中）
     - `rules`：制品特定规则（对你的约束——不要包含在输出中）
     - `template`：用于输出文件的结构
     - `instruction`：模式特定指导
     - `outputPath`：在哪里写制品
     - `dependencies`：需要阅读的已完成制品
   - **创建制品文件**：
     - 读取任何已完成的依赖文件以获取上下文
     - 使用 `template` 作为结构——填写其部分
     - 在写入时应用 `context` 和 `rules` 作为约束——但不要将它们复制到文件中
     - 写入指令中指定的输出路径
   - 显示创建了什么以及现在解锁了什么
   - 创建 **一个** 制品后停止

   ---

   **如果没有制品准备好（全部被阻塞）**：
   - 这在有效模式下不应该发生
   - 显示状态并建议检查问题

4. **创建制品后，显示进度**
   ```bash
   openspec status --change "<name>"
   ```

**输出**

每次调用后显示：
- 创建了哪个制品
- 正在使用的工作流程模式
- 当前进度（N/M 完成）
- 现在解锁了哪些制品
- 提示："运行 `/opsx:continue` 创建下一个制品"

**制品创建指南**

制品类型及其目的取决于模式。使用指令输出中的 `instruction` 字段来了解要创建什么。

常见的制品模式：

**spec-driven 模式**（proposal → specs → design → tasks）：
- **proposal.md**：如果不清楚，询问用户。填写 Why、What Changes、Capabilities、Impact。
  - Capabilities 部分至关重要——列出的每个能力都需要一个规范文件。
- **specs/<capability>/spec.md**：为 proposal 的 Capabilities 部分中列出的每个能力创建一个规范（使用能力名称，而不是变更名称）。
- **design.md**：记录技术决策、架构和实现方法。
- **tasks.md**：将实现分解为带复选框的任务。

对于其他模式，遵循 CLI 输出的 `instruction` 字段。

**护栏**
- 每次调用创建一个制品
- 始终在创建新制品之前阅读依赖制品
- 永不跳过制品或乱序创建
- 如果上下文不清楚，在创建之前询问用户
- 写入后验证制品文件存在
- 使用模式的制品序列，不要假设特定的制品名称
- **重要**：`context` 和 `rules` 是对你的约束，不是文件的内容
  - 不要将 `<context>`、`<rules>`、`<project_context>` 块复制到制品中
  - 这些指导你写什么，但不应该出现在输出中
