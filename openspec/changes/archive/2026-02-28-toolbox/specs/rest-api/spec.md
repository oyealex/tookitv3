## ADDED Requirements

### Requirement: 统一响应格式
系统 SHALL 提供统一的 RESTful API 响应格式 `Result<T>`，包含以下字段：
- `code`: 状态码（整型）
- `message`: 响应消息（字符串）
- `data`: 响应数据（泛型）

#### Scenario: 成功响应
- **WHEN** API 处理成功
- **THEN** 返回 `code=200`、`message="success"` 和对应的 `data`

#### Scenario: 错误响应
- **WHEN** API 处理失败
- **THEN** 返回非 200 的 `code` 和描述性的 `message`

### Requirement: 全局异常处理
系统 SHALL 通过 `@ControllerAdvice` 实现全局异常处理，统一异常响应格式。

#### Scenario: 处理未知异常
- **WHEN** 发生未捕获的异常
- **THEN** 返回 `code=500`、`message="Internal Server Error"`、`data=null`

#### Scenario: 处理业务异常
- **WHEN** 抛出业务异常
- **THEN** 返回业务异常中定义的 `code` 和 `message`

### Requirement: 健康检查端点
系统 SHALL 提供一个健康检查端点，用于验证服务是否正常运行。

#### Scenario: 健康检查成功
- **WHEN** 访问健康检查端点
- **THEN** 返回 `code=200`、`message="success"`、`data` 包含服务状态信息