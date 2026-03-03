## Context

工具箱需要支持调用外部独立进程的运维工具来执行操作。当前系统仅有本地设备管理功能，缺乏扩展外部工具的能力。外部工具作为独立进程运行，通过文件系统与工具箱进行数据交换。

关键约束：
- 工具以独立进程形式运行
- 进程间通信通过临时 JSON 文件完成
- ID 必须符合英文、数字、下划线、短横线组成，不超过64字符
- 相同 ID 的工具不能并存
- 工具在 tools 目录下按子目录组织，自动发现，不提供注册接口
- 工具运行不考虑超时（可能运行相当长时间）

## Goals / Non-Goals

**Goals:**
- 实现工具自动发现机制（启动时扫描 tools 目录）
- 定义 tool.json 配置文件格式
- 实现工具调用器，通过临时文件传递设备信息和结果
- 根据平台（Windows/Linux）使用不同的可执行文件
- 实现运维结果和交付件的持久化存储
- 实现运行锁机制，防止并发运行
- 实现结果监控，自动检测工具执行完毕

**Non-Goals:**
- 不实现具体的工具实现（由外部提供）
- 不提供工具的手动注册接口
- 不提供工具的 Web 管理界面
- 设备锁由设备管理模块实现

## Decisions

### 1. 工具自动发现机制
**决定**: 应用启动时扫描 `tools` 目录下的一级子目录
- 目录名称即为工具 ID
- 每个子目录必须包含 tool.json 配置文件
- 可执行文件由 tool.json 中的 command 字段指定

### 2. tool.json 配置格式
```json
{
  "id": "device-ping",
  "name": "设备Ping工具",
  "version": "1.0.0",
  "description": "用于检测设备网络连通性",
  "command": {
    "windows": "ping.bat",
    "linux": "ping.sh"
  }
}
```
- id: 工具 ID（与目录名一致）
- name: 显示名称
- version: 语义化版本号
- command: 可执行命令（支持平台区分）
  - 字符串形式：所有平台共用同一命令
  - 对象形式：{ "windows": "xxx", "linux": "xxx" }

### 3. 可执行文件支持
**决定**: 支持以下可执行文件格式
- Windows: .exe, .bat, .cmd
- Linux: 无扩展名（需设置可执行权限）, .sh

### 4. 进程间通信机制
**决定**: 工具箱在启动工具时创建工作目录，并将目录路径作为命令行参数传递

命令行参数格式：
```
[工具可执行文件] --work-dir "data/archive/device-ping-20260303233401123"
```

工作目录结构：
```
data/archive/device-ping-20260303233401123/
├── input.json      # 输入 JSON 文件
├── result.json    # 结果 JSON 文件（由工具写入）
└── artifacts/     # 交付件目录（由工具创建）
```

输入 JSON 格式（input.json）：
```json
{
  "toolId": "device-ping",
  "toolName": "设备Ping工具",
  "startTime": "2026-03-03 23:34:01.123",
  "workDir": "data/archive/device-ping-20260303233401123",
  "devices": [
    {
      "ip": "192.168.1.1",
      "name": "服务器A",
      "port": 22,
      "username": "admin",
      "password": "password123"
    }
  ]
}
```

- devices 包含完整设备信息，包括登录密码
- 密码当前为明文，未来会扩展为加密后的密文

### 5. 运维结果存储
**决定**: 每次工具运行生成独立的存档目录 `data/archive/{toolid}-{timestamp}/`

时间戳格式：
- 目录名称中的 timestamp：`20260303233450123`（17位，精确到毫秒，yyyyMMddHHmmssSSS）
- JSON 中的时间字段：`2026-03-03 23:34:01.123`（yyyy-MM-dd HH:mm:ss.SSS）

目录命名规则：
- 主格式：`{toolid}-{timestamp}/`
- 如遇重复，追加递增序号：`{toolid}-{timestamp}-1/`, `{toolid}-{timestamp}-2/` 等

结果 JSON 格式（result.json）：
```json
{
  "toolId": "device-ping",
  "toolName": "设备Ping工具",
  "startTime": "2026-03-03 23:34:01.123",
  "endTime": "2026-03-03 23:34:05.456",
  "status": "completed",
  "result": "success",
  "deviceResults": [
    { "ip": "192.168.1.1", "status": "success", "message": "ping成功" },
    { "ip": "192.168.1.2", "status": "failed", "message": "超时" }
  ],
  "artifacts": [
    { "name": "report.pdf", "path": "report.pdf", "size": 1024 }
  ]
}
```

- status: 工具执行状态，当前固定为 `completed`（由工具箱监控检测）
- result: 工具执行结果，`success` 或 `failed`
- deviceResults: 每个设备的运维结果，使用 ip 标识设备，状态为 success/failed/not_executed
- artifacts: 交付件列表，记录在结果 JSON 中

### 6. 工具执行监控
**决定**: 工具箱通过定时轮询 result.json 中的 status 字段检测执行完毕
- 轮询间隔：每秒检查一次
- 检测到 status 为 "completed" 时认为工具执行完毕
- 执行完毕后：
  - 释放全局运行锁
  - 通知设备管理模块解锁设备
  - 返回执行结果

### 7. 运行锁机制
**决定**: 锁机制分为两层
- 全局锁：防止同时运行多个工具（Tool 模块自身实现）
- 设备锁：锁定运维中的设备，防止变更、删除（由 Device 模块实现）

设备锁接口（由 Device 模块提供）：
- `lockDevices(List<String> ips)` - 锁定设备列表
- `unlockDevices(List<String> ips)` - 解锁设备列表
- `isLocked(String ip)` - 检查设备是否被锁定

Tool 模块负责：
- 调用 Device 模块的锁接口
- 在工具启动前锁定设备
- 在工具执行完毕后解锁设备

### 8. 工具调用接口
**决定**: 定义 Tool 接口和 ToolInvoker 接口
- Tool: 从 tool.json 加载的工具元数据
- ToolInvoker: 执行工具进程

### 9. 操作日志
**决定**: 工具模块的关键操作均记录操作日志

日志场景：
| 操作 | 日志类型 | 记录内容 |
|------|----------|----------|
| 工具扫描 | INFO | 扫描开始、扫描完成、发现工具数量、跳过无效目录原因 |
| 工具启动 | INFO | 工具ID、选中设备列表、工作目录、启动时间 |
| 工具结束 | INFO | 工具ID、执行结果（success/failed）、结束时间、耗时 |

日志记录使用现有的 OperationLog 框架，记录操作人、操作类型、操作结果等信息。

## Risks / Trade-offs

- [风险]: tools 目录不存在 → [缓解]: 启动时创建默认目录]
- [风险]: tool.json 格式错误 → [缓解]: 启动时校验并记录错误，跳过无效工具]
- [风险]: 目录重复 → [缓解]: 追加递增序号]
- [风险]: 平台命令配置缺失 → [缓解]: 优先使用 command 字符串，无对象时使用默认命令]
- [风险]: 并发调用工具 → [缓解]: 使用全局锁阻止]
- [风险]: 工具运行期间设备被删除 → [缓解]: 设备锁由 Device 模块保护]
- [风险]: 工具异常退出未写入 result.json → [缓解]: 超时后强制终止（可选扩展）]
- [权衡]: 同步调用 vs 异步调用 - 当前采用轮询检测，异步等待工具完成
- [权衡]: 存储空间管理 - 不主动删除存档，依赖外部清理