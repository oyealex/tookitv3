## ADDED Requirements

### Requirement: OpenAPI 文档自动生成
系统 SHALL 集成 SpringDoc OpenAPI，自动生成 OpenAPI 3.0 规范文档。

#### Scenario: 访问 OpenAPI JSON 文档
- **WHEN** 访问 `/v3/api-docs` 端点
- **THEN** 返回符合 OpenAPI 3.0 规范的 JSON 文档

### Requirement: Swagger UI 界面
系统 SHALL 提供 Swagger UI 界面，用于可视化查看和测试 API。

#### Scenario: 访问 Swagger UI
- **WHEN** 访问 `/swagger-ui.html` 或 `/swagger-ui/index.html`
- **THEN** 显示 Swagger UI 界面，列出所有已定义的 API 端点

### Requirement: API 元数据配置
系统 SHALL 配置 OpenAPI 元数据，包括：
- API 标题
- API 描述
- API 版本
- 联系信息

#### Scenario: 验证 API 元数据
- **WHEN** 访问 OpenAPI 文档
- **THEN** 文档包含正确配置的标题、描述、版本信息