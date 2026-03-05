---
name: "OPSX: 新建"
description: "使用实验性制品工作流程开始一个新的变更"
category: Workflow
tags: [workflow, artifacts, experimental]
---

使用实验性制品驱动的方法开始一个新的变更。

**输入**：`/opsx:new` 之后的参数是变更名称（kebab-case），或者是用户想要构建的内容的描述。

**步骤**

1. **如果没有输入，询问他们想要构建什么**

   使用 **AskUserQuestion 工具**（开放式的，没有预设选项）询问：
   > "您想要处理什么变更？描述您想要构建或修复的内容。"

   从他们的描述中派生一个 kebab-case 名称（例如"add user authentication" → `add-user-auth`）。

   **重要**：不要在不理解用户想要构建什么的情况下继续。

2. **确定工作流程模式**

   使用默认模式（省略 `--schema`），除非用户明确请求不同的工作流程。

   **只有在用户提及以下内容时才使用不同的模式：**
   - 特定的模式名称 → 使用 `--schema <name>`
   - "show workflows" 或 "what workflows" → 运行 `openspec schemas --json` 让他们选择

   **否则**：省略 `--schema` 使用默认模式。

3. **创建变更目录**
   ```bash
   openspec new change "<name>"
   ```
   只有在用户请求了特定工作流程时才添加 `--schema <name>`。
   这将在 `openspec/changes/<name>/` 创建一个带有选定模式的脚手架变更。

4. **显示制品状态**
   ```bash
   openspec status --change "<name>"
   ```
   这显示需要创建哪些制品以及哪些已就绪（依赖已满足）。

5. **获取第一个制品的指令**
   第一个制品取决于模式。检查状态输出以找到第一个状态为 "ready" 的制品。
   ```bash
   openspec instructions <first-artifact-id> --change "<name>"
   ```
   这输出用于创建第一个制品的模板和上下文。

6. **停止并等待用户方向**

**输出**

完成步骤后，总结：
- 变更名称和位置
- 正在使用的模式/工作流程及其制品序列
- 当前状态（0/N 制品完成）
- 第一个制品的模板
- 提示："准备好创建第一个制品了吗？运行 `/opsx:continue` 或者只需描述这个变更是关于什么的，我会起草它。"

**护栏**
- 暂时不要创建任何制品——只显示指令
- 不要超越显示第一个制品模板
- 如果名称无效（不是 kebab-case），请求有效的名称
- 如果已存在同名变更，建议使用 `/opsx:continue`
- 如果使用非默认工作流程，传递 --schema
