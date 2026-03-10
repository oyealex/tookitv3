# Device Management (Delta Spec)

## Purpose

扩展现有设备管理能力，增加设备锁定来源支持，允许场景模块按场景维度锁定和管理设备。

## ADDED Requirements

### Requirement: 设备锁定来源支持
系统 SHALL 扩展设备锁定机制，支持记录锁定来源（如场景 ID）。

#### Scenario: 锁定设备时记录来源
- **WHEN** 调用锁定接口，传入设备 IP 列表和锁定来源（如场景 ID）
- **THEN** 系统 SHALL 记录每个被锁定设备的锁定来源和锁定时间

#### Scenario: 按来源解锁设备
- **WHEN** 调用按来源解锁接口，传入锁定来源（如场景 ID）
- **THEN** 系统 SHALL 解锁所有由该来源锁定的设备

#### Scenario: 查询设备锁定信息
- **WHEN** 查询设备详情且设备被锁定
- **THEN** 返回结果 SHALL 包含锁定来源和锁定时间

### Requirement: 设备锁定接口扩展
系统 SHALL 扩展 DeviceLockService 接口，支持锁定来源参数。

#### Scenario: lockDevices 扩展签名
- **WHEN** 调用 lockDevices(ips, lockSource)
- **THEN** 系统 SHALL 锁定设备并记录锁定来源

#### Scenario: unlockBySource 接口
- **WHEN** 调用 unlockBySource(lockSource)
- **THEN** 系统 SHALL 解锁所有由该来源锁定的设备

#### Scenario: getLockInfo 接口
- **WHEN** 调用 getLockInfo(ip)
- **THEN** 系统 SHALL 返回设备的锁定信息（lockedBy、lockedAt）或 null（未锁定时）

### Requirement: 设备列表查询锁定状态筛选
系统 SHALL 支持按锁定状态筛选设备列表。

#### Scenario: 筛选已锁定设备
- **WHEN** 查询设备列表时指定 locked=true
- **THEN** 返回所有被锁定的设备

#### Scenario: 筛选未锁定设备
- **WHEN** 查询设备列表时指定 locked=false
- **THEN** 返回所有未被锁定的设备