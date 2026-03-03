## ADDED Requirements

### Requirement: 导出设备到Excel
系统 SHALL 提供 REST API 将设备列表导出为Excel文件。

#### Scenario: 成功导出全部设备
- **WHEN** 请求导出设备列表
- **THEN** 下载包含所有设备信息的Excel文件

#### Scenario: 按条件筛选导出
- **WHEN** 请求导出并指定筛选条件
- **THEN** 下载包含符合条件设备的Excel文件

#### Scenario: 导出空列表
- **WHEN** 没有设备数据可导出
- **THEN** 下载包含表头的空Excel文件

### Requirement: Excel文件格式
系统 SHALL 使用标准Excel格式导出设备数据。

#### Scenario: Excel表头格式
- **WHEN** 导出设备到Excel
- **THEN** Excel文件包含以下列：设备名称、IP地址、设备类型、型号、版本、登录用户名、创建时间、更新时间

#### Scenario: 导出不包含密码
- **WHEN** 导出设备到Excel
- **THEN** Excel文件不包含密码字段

#### Scenario: Excel数据格式
- **WHEN** 导出设备到Excel
- **THEN** 时间字段格式为 yyyy-MM-dd HH:mm:ss

### Requirement: 从Excel导入设备
系统 SHALL 提供 REST API 从Excel文件批量导入设备。

#### Scenario: 成功导入设备
- **WHEN** 上传格式正确的Excel文件
- **THEN** 返回成功数量、失败数量、失败原因（Top 10）

#### Scenario: 导入时检查总数限制
- **WHEN** 导入后设备总数将超过 1000 台
- **THEN** 返回错误提示，说明当前数量和可导入数量

#### Scenario: Excel格式错误
- **WHEN** 上传的Excel文件格式不正确或缺少必填列
- **THEN** 返回错误提示，包含缺少的列名

#### Scenario: 导入数据验证失败
- **WHEN** Excel中包含无效数据
- **THEN** 返回失败详情，包含行号和错误原因

#### Scenario: 导入时IP重复
- **WHEN** Excel中的IP地址与已有设备重复
- **THEN** 返回失败详情，标记重复的行号和IP地址

### Requirement: Excel模板下载
系统 SHALL 提供 Excel 模板下载功能。

#### Scenario: 下载导入模板
- **WHEN** 请求下载设备导入模板
- **THEN** 返回包含表头、字段说明和示例数据的Excel模板文件

#### Scenario: 模板包含字段说明
- **WHEN** 下载Excel模板
- **THEN** 模板第二行为字段说明（如"必填，IPv4格式"、"选填，最长120字符"）

#### Scenario: 模板包含示例数据
- **WHEN** 下载Excel模板
- **THEN** 模板第三行为示例数据行

### Requirement: 大文件导入处理
系统 SHALL 支持大文件Excel导入，无数量限制。

#### Scenario: 流式处理大文件
- **WHEN** 导入大量设备数据
- **THEN** 使用流式读取处理，避免内存溢出

### Requirement: 错误信息包含行号
系统 SHALL 在 Excel 导入导出错误信息中包含 Excel 行号。

#### Scenario: 导入错误包含行号
- **WHEN** Excel导入过程中发生错误
- **THEN** 错误信息包含对应的 Excel 行号

#### Scenario: 批量操作失败详情
- **WHEN** 批量导入部分失败
- **THEN** 返回失败原因列表，每条包含行号、IP和原因，最多显示 Top 10