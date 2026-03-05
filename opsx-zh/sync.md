---
name: "OPSX: 同步"
description: "将增量规范从变更同步到主规范"
category: Workflow
tags: [workflow, specs, experimental]
---

将增量规范从变更同步到主规范。

这是一个**代理驱动**的操作——你将读取增量规范并直接编辑主规范以应用更改。这允许智能合并（例如，添加场景而不复制整个需求）。

**输入**：可选择在 `/opsx:sync` 后指定变更名称（例如 `/opsx:sync add-auth`）。如果省略，检查是否可以从对话上下文中推断。如果模糊或不确定，你必须提示选择可用变更。

**步骤**

1. **如果未提供变更名称，提示选择**

   运行 `openspec list --json` 获取可用变更。使用 **AskUserQuestion 工具** 让用户选择。

   显示具有增量规范（在 `specs/` 目录下）的变更。

   **重要**：不要猜测或自动选择变更。始终让用户选择。

2. **查找增量规范**

   在 `openspec/changes/<name>/specs/*/spec.md` 中查找增量规范文件。

   每个增量规范文件包含如下部分：
   - `## ADDED Requirements` - 要添加的新需求
   - `## MODIFIED Requirements` - 对现有需求的更改
   - `## REMOVED Requirements` - 要删除的需求
   - `## RENAMED Requirements` - 要重命名的需求（FROM:/TO: 格式）

   如果未找到增量规范，通知用户并停止。

3. **对于每个增量规范，将更改应用到主规范**

   对于在 `openspec/changes/<name>/specs/<capability>/spec.md` 处有增量规范的每个能力：

   a. **读取增量规范**以了解预期的更改

   b. **读取主规范** at `openspec/specs/<capability>/spec.md`（可能尚不存在）

   c. **智能应用更改**：

      **ADDED Requirements：**
      - 如果主规范中不存在需求 → 添加它
      - 如果需求已存在 → 更新它以匹配（视为隐式 MODIFIED）

      **MODIFIED Requirements：**
      - 在主规范中找到需求
      - 应用更改 - 这可以是：
        - 添加新场景（不需要复制现有场景）
        - 修改现有场景
        - 更改需求描述
      - 保留 delta 中未提及的场景/内容

      **REMOVED Requirements：**
      - 从主规范中删除整个需求块

      **RENAMED Requirements：**
      - 找到 FROM 需求，重命名为 TO

   d. **如果能力尚不存在则创建新的主规范**：
      - 创建 `openspec/specs/<capability>/spec.md`
      - 添加 Purpose 部分（可以简短，标记为 TBD）
      - 添加带有 ADDED 需求的 Requirements 部分

4. **显示摘要**

   应用所有更改后，总结：
   - 更新了哪些能力
   - 做了哪些更改（添加/修改/删除/重命名需求）

**增量规范格式参考**

```markdown
## ADDED Requirements

### Requirement: New Feature
系统应该做新事情。

#### Scenario: Basic case
- **WHEN** user does X
- **THEN** system does Y

## MODIFIED Requirements

### Requirement: Existing Feature
#### Scenario: New scenario to add
- **WHEN** user does A
- **THEN** system does B

## REMOVED Requirements

### Requirement: Deprecated Feature

## RENAMED Requirements

- FROM: `### Requirement: Old Name`
- TO: `### Requirement: New Name`
```

**关键原则：智能合并**

与程序化合并不同，你可以应用**部分更新**：
- 要添加场景，只需在 MODIFIED 下包含该场景——不要复制现有场景
- delta 代表*意图*，而不是整体替换
- 使用你的判断来合理地合并更改

**成功时的输出**

```
## 规范已同步：<change-name>

已更新主规范：

**<capability-1>**：
- 添加需求："New Feature"
- 修改需求："Existing Feature"（添加了 1 个场景）

**<capability-2>**：
- 创建了新的规范文件
- 添加需求："Another Feature"

主规范已更新。变更保持活动状态——在实现完成后归档。
```

**护栏**
- 在进行更改之前读取增量规范和主规范
- 保留 delta 中未提及的现有内容
- 如果 something 不清楚，请求澄清
- 随你显示你正在更改什么
- 操作应该是幂等的——运行两次应该得到相同的结果
