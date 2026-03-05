---
name: "OPSX: 提议"
description: "提议新变更 - 一步创建它并生成所有制品"
category: Workflow
tags: [workflow, artifacts, experimental]
---

提议新变更——一步创建变更并生成所有制品。

我将创建一个包含以下制品的变更：
- proposal.md（什么和为什么）
- design.md（如何）
- tasks.md（实现步骤）

准备好实现时，运行 /opsx:apply

---

**输入**：`/opsx:propose` 之后的参数是变更名称（kebab-case），或者是用户想要构建的内容的描述。

**步骤**

1. **如果没有输入，询问他们想要构建什么**

   使用 **AskUserQuestion 工具**（开放式的，没有预设选项）询问：
   > "您想要处理什么变更？描述您想要构建或修复的内容。"

   从他们的描述中派生一个 kebab-case 名称（例如"add user authentication" → `add-user-auth`）。

   **重要**：不要在不理解用户想要构建什么的情况下继续。

2. **创建变更目录**
   ```bash
   openspec new change "<name>"
   ```
   这将在 `openspec/changes/<name>/` 创建一个脚手架变更，带有 `.openspec.yaml`。

3. **获取制品构建顺序**
   ```bash
   openspec status --change "<name>" --json
   ```
   解析 JSON 以获取：
   - `applyRequires`：实现前需要的制品 ID 数组（例如 `["tasks"]`）
   - `artifacts`：所有制品及其状态和依赖的列表

4. **按顺序创建制品直到可以应用**

   使用 **TodoWrite 工具** 跟踪制品进度。

   按依赖顺序循环遍历制品（没有待处理依赖的制品优先）：

   a. **对于每个 `ready` 的制品**（依赖已满足）：
      - 获取指令：
        ```bash
        openspec instructions <artifact-id> --change "<name>" --json
        ```
      - 指令 JSON 包括：
        - `context`：项目背景（对你的约束——不要包含在输出中）
        - `rules`：制品特定规则（对你的约束——不要包含在输出中）
        - `template`：用于输出文件的结构
        - `instruction`：此制品类型的模式特定指导
        - `outputPath`：在哪里写制品
        - `dependencies`：需要阅读的已完成制品
      - 读取任何已完成的依赖文件以获取上下文
      - 使用 `template` 作为结构创建制品文件
      - 应用 `context` 和 `rules` 作为约束——但不要将它们复制到文件中
      - 显示简短进度："Created <artifact-id>"

   b. **继续直到所有 `applyRequires` 制品完成**
      - 创建每个制品后，重新运行 `openspec status --change "<name>" --json`
      - 检查 `applyRequires` 中的每个制品 ID 是否在制品数组中具有 `status: "done"`
      - 当所有 `applyRequires` 制品都完成时停止

   c. **如果制品需要用户输入**（上下文不清楚）：
      - 使用 **AskUserQuestion 工具** 澄清
      - 然后继续创建

5. **显示最终状态**
   ```bash
   openspec status --change "<name>"
   ```

**输出**

完成所有制品后，总结：
- 变更名称和位置
- 创建的制品列表及简要描述
- 什么已就绪："所有制品已创建！准备实现。"
- 提示："运行 `/opsx:apply` 开始实现。"

**制品创建指南**

- 遵循 `openspec instructions` 中每个制品类型的 `instruction` 字段
- 模式定义每个制品应该包含什么——遵循它
- 在创建新制品之前阅读依赖制品以获取上下文
- 使用 `template` 作为输出文件的结构——填写其部分
- **重要**：`context` 和 `rules` 是对你的约束，不是文件的内容
  - 不要将 `<context>`、`<rules>`、`<project_context>` 块复制到制品中
  - 这些指导你写什么，但不应该出现在输出中

**护栏**
- 创建实现所需的所有制品（由模式的 `apply.requires` 定义）
- 始终在创建新制品之前阅读依赖制品
- 如果上下文严重不清楚，询问用户——但更倾向于做出合理决策以保持势头
- 如果已存在同名变更，询问用户是想继续还是创建新的
- 在继续下一个之前，写入后验证每个制品文件存在
