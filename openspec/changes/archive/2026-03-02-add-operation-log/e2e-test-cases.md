# 操作日志模块 - 接口端到端验收测试

## 测试场景一：记录操作日志

### T-001: 同步记录创建设备操作日志
**前置条件**: 系统正常运行，数据库连接正常

**测试步骤**:
1. 发送 POST /api/v1/devices 创建设备 {"ip": "192.168.1.100", "type": "SERVER", "password": "admin123", "name": "测试服务器"}
2. 验证返回 200，设备创建成功
3. 发送 GET /api/v1/operation-logs 查询操作日志
4. 验证返回结果中包含最新一条记录
5. 验证该记录: operationType=CREATE, objectType=DEVICE, objectId=192.168.1.100, result=SUCCESS

**预期结果**: 操作日志正确记录创建操作

---

### T-002: 异步记录删除设备操作日志
**前置条件**: 系统正常运行，已存在 IP 为 192.168.1.101 的设备

**测试步骤**:
1. 发送 DELETE /api/v1/devices/192.168.1.101 删除设备
2. 验证返回 200，设备删除成功（不等待异步日志写入）
3. 等待 2 秒（让异步写入完成）
4. 发送 GET /api/v1/operation-logs?keyword=192.168.1.101 查询
5. 验证返回结果中包含该删除记录
6. 验证: operationType=DELETE, result=SUCCESS

**预期结果**: 异步日志在延迟后正确写入数据库

---

### T-003: 记录失败的操作日志
**前置条件**: 系统正常运行

**测试步骤**:
1. 发送 POST /api/v1/devices 创建设备，使用无效 IP 格式
2. 验证返回 400 错误
3. 发送 GET /api/v1/operation-logs?result=FAILURE 查询失败日志
4. 验证返回结果中包含该失败记录
5. 验证: result=FAILURE, failureReason 包含错误信息

**预期结果**: 失败操作正确记录 failureReason

---

## 测试场景二：查询操作日志

### T-004: 分页查询操作日志
**前置条件**: 数据库中已有至少 25 条操作日志

**测试步骤**:
1. 发送 GET /api/v1/operation-logs?offset=0&limit=20
2. 验证返回 200
3. 验证返回结果中 list 数组长度为 20
4. 验证返回结果中 total >= 25
5. 发送 GET /api/v1/operation-logs?offset=20&limit=20
6. 验证返回结果中 list 数组长度为 <= 5

**预期结果**: 分页功能正常，返回总数和分页数据

---

### T-005: 关键字搜索
**前置条件**: 数据库中已有设备名称为 "测试服务器" 的操作日志

**测试步骤**:
1. 发送 GET /api/v1/operation-logs?keyword=测试服务器
2. 验证返回 200
3. 验证返回的所有记录 objectName 包含 "测试服务器"

**预期结果**: 关键字搜索返回匹配结果

---

### T-006: 时间范围筛选
**前置条件**: 数据库中已有历史操作日志

**测试步骤**:
1. 获取当前时间作为 endTime
2. 获取 1 小时前的时间作为 startTime
3. 发送 GET /api/v1/operation-logs?startTime={startTime}&endTime={endTime}
4. 验证返回 200
5. 验证返回的所有记录 operationTime 在指定范围内

**预期结果**: 时间范围筛选正确

---

### T-007: 操作结果筛选
**前置条件**: 数据库中已有成功和失败的操作日志

**测试步骤**:
1. 发送 GET /api/v1/operation-logs?result=SUCCESS
2. 验证返回 200
3. 验证返回的所有记录 result=SUCCESS
4. 发送 GET /api/v1/operation-logs?result=FAILURE
5. 验证返回的所有记录 result=FAILURE

**预期结果**: 按操作结果筛选正确

---

### T-008: 组合条件查询
**前置条件**: 数据库中已有相关操作日志

**测试步骤**:
1. 发送 GET /api/v1/operation-logs?keyword=测试&result=SUCCESS&offset=0&limit=10
2. 验证返回 200
3. 验证返回的所有记录同时满足:
   - objectName 包含 "测试"
   - result = SUCCESS

**预期结果**: 组合条件查询正确

---

### T-009: 排序功能
**前置条件**: 数据库中已有操作日志

**测试步骤**:
1. 发送 GET /api/v1/operation-logs?sortBy=operationTime&sortOrder=desc&limit=10
2. 验证返回 200
3. 验证返回的记录按 operationTime 降序排列
4. 发送 GET /api/v1/operation-logs?sortBy=objectName&sortOrder=asc&limit=10
5. 验证返回的记录按 objectName 升序排列

**预期结果**: 排序功能正常

---

### T-010: limit 超限错误
**前置条件**: 系统正常运行

**测试步骤**:
1. 发送 GET /api/v1/operation-logs?limit=101
2. 验证返回 400 错误
3. 验证错误消息包含 "单次查询数量不能超过 100 条"

**预期结果**: 正确返回分页超限错误

---

## 测试场景三：导出操作日志

### T-011: 导出全部操作日志
**前置条件**: 数据库中已有操作日志

**测试步骤**:
1. 发送 GET /api/v1/operation-logs/export
2. 验证返回 200
3. 验证返回内容为 Excel 文件 (application/vnd.openxmlformats-officedocument.spreadsheetml.sheet)
4. 验证 Excel 文件可正常打开并包含数据

**预期结果**: 导出功能正常，返回有效的 Excel 文件

---

### T-012: 导出筛选后的操作日志
**前置条件**: 数据库中已有操作日志

**测试步骤**:
1. 发送 GET /api/v1/operation-logs/export?result=SUCCESS
2. 验证返回 200
3. 验证导出的 Excel 文件中所有记录 result=SUCCESS

**预期结果**: 导出支持筛选条件

---

## 测试场景四：国际化

### T-013: 中文环境记录操作日志
**前置条件**: Accept-Language 设置为 zh-CN

**测试步骤**:
1. 设置请求头 Accept-Language: zh-CN
2. 发送 POST /api/v1/devices 创建设备 {"ip": "192.168.1.200", "type": "STORAGE", "password": "admin", "name": "存储设备"}
3. 等待异步写入完成
4. 发送 GET /api/v1/operation-logs?keyword=存储设备
5. 验证返回的记录 description 为中文描述（如 "创建设备: 存储设备"）

**预期结果**: 中文环境下使用中文描述

---

### T-014: 英文环境记录操作日志
**前置条件**: Accept-Language 设置为 en-US

**测试步骤**:
1. 设置请求头 Accept-Language: en-US
2. 发送 POST /api/v1/devices 创建设备 {"ip": "192.168.1.201", "type": "STORAGE", "password": "admin", "name": "Storage Device"}
3. 等待异步写入完成
4. 发送 GET /api/v1/operation-logs?keyword=Storage Device
5. 验证返回的记录 description 为英文描述（如 "Create device: Storage Device"）

**预期结果**: 英文环境下使用英文描述

---

## 测试场景五：注解功能

### T-015: AOP 注解自动记录日志
**前置条件**: DeviceController 方法已配置 @OperationLog 注解

**测试步骤**:
1. 发送 POST /api/v1/devices 创建设备 {"ip": "192.168.1.202", "type": "NETWORK", "password": "admin", "name": "网络设备"}
2. 验证返回 200
3. 发送 GET /api/v1/operation-logs?keyword=网络设备
4. 验证自动记录了操作日志
5. 验证自动填充了正确的字段值

**预期结果**: 注解方式自动记录操作日志

---

## 测试场景六：权限和安全

### T-016: DELETE 操作返回 405
**前置条件**: 系统正常运行

**测试步骤**:
1. 发送 DELETE /api/v1/operation-logs/1
2. 验证返回 405 Method Not Allowed

**预期结果**: 不支持删除操作日志

---

### T-017: SQL 注入防护
**前置条件**: 系统正常运行

**测试步骤**:
1. 发送 GET /api/v1/operation-logs?keyword=' OR '1'='1
2. 验证返回 200 或 400（不应返回 500 或导致数据泄露）
3. 验证返回结果安全，不会执行恶意 SQL

**预期结果**: 参数化查询防止 SQL 注入

---

## 测试场景七：缓存和持久化

### T-018: 应用重启后缓存恢复
**前置条件**: 应用正常运行，已产生未写入数据库的操作日志

**测试步骤**:
1. 发送多个创建设备请求（异步日志写入缓存）
2. **不等待**异步写入完成，直接**重启应用**
3. 重启后等待几秒
4. 发送 GET /api/v1/operation-logs 查询
5. 验证之前创建的日志记录存在（从磁盘恢复）

**预期结果**: 重启后缓存数据不丢失

---

### T-019: 大量操作日志写入性能
**前置条件**: 系统正常运行

**测试步骤**:
1. 记录开始时间
2. 循环发送 100 次创建设备请求（每次使用不同 IP）
3. 记录结束时间
4. 等待异步写入完成
5. 验证所有 100 条日志都已写入数据库
6. 验证总耗时 < 10 秒

**预期结果**: 异步写入机制性能良好

---

## 测试场景八：数据完整性

### T-020: objectExtra JSON 字段存储
**前置条件**: 系统正常运行

**测试步骤**:
1. 创建设备
2. 查询操作日志
3. 验证 objectExtra 字段为有效 JSON 格式
4. 验证可解析 JSON 获取额外信息

**预期结果**: JSON 字段正确存储和解析

---

### T-021: 操作日志包含完整信息
**前置条件**: 系统正常运行

**测试步骤**:
1. 创建设备
2. 查询操作日志
3. 验证返回的每条记录包含所有字段:
   - id, operationTime, operationType, objectType
   - objectId, objectName, objectExtra, description
   - result, failureReason, operator, operatorIp, createdAt

**预期结果**: 返回完整的操作日志信息

---

## 测试场景九：设备管理全流程集成

### T-022: 设备完整生命周期日志记录
**前置条件**: 系统正常运行，数据库清空或已知状态

**测试步骤**:
1. **创建**: POST /api/v1/devices {"ip": "192.168.1.50", "type": "SERVER", "password": "admin", "name": "生命周期测试"}
2. **查询创建日志**: GET /api/v1/operation-logs?keyword=生命周期测试
3. 验证 operationType=CREATE, result=SUCCESS

4. **更新**: PUT /api/v1/devices/192.168.1.50 {"name": "生命周期测试-已更新"}
5. **查询更新日志**: GET /api/v1/operation-logs?keyword=生命周期测试-已更新
6. 验证 operationType=UPDATE, result=SUCCESS

7. **删除**: DELETE /api/v1/devices/192.168.1.50
8. **查询删除日志**: GET /api/v1/operation-logs?keyword=192.168.1.50 (查找最新删除记录)
9. 验证 operationType=DELETE, result=SUCCESS

10. **汇总**: 查询所有该设备的操作日志，验证包含 CREATE、UPDATE、DELETE 三种类型

**预期结果**: 设备完整生命周期（创建→更新→删除）都有对应的操作日志记录

---

### T-023: 批量操作日志记录
**前置条件**: 系统正常运行

**测试步骤**:
1. 批量创建设备（10 台）
2. 查询操作日志
3. 验证记录了 IMPORT 类型操作（批量导入视为一种特殊操作类型）或每台设备的 CREATE 记录
4. 验证日志包含批量操作的关键信息

**预期结果**: 批量操作的日志正确记录

---

## 验收通过标准

| 指标 | 标准 |
|------|------|
| 测试用例通过率 | 100% (23/23) |
| 接口响应时间 | < 500ms（单次查询） |
| 数据准确性 | 所有字段正确 |
| 国际化 | 中英文切换正确 |
| 缓存恢复 | 重启后数据不丢失 |

---

**测试优先级建议**:
- P0（必须通过）: T-001, T-002, T-004, T-016, T-022
- P1（重要）: T-003, T-005, T-006, T-007, T-011, T-013, T-014
- P2（建议）: T-008, T-009, T-010, T-012, T-015, T-017, T-018, T-019, T-020, T-021, T-023