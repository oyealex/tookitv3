## Context

创建一个全新的 Spring Boot 项目模板，作为后续开发的基础。项目使用 Java 21、Maven 构建，集成 OpenAPI 文档生成能力。目标是一个干净、可扩展的项目结构。

## Goals / Non-Goals

**Goals:**
- 建立标准化的项目目录结构
- 配置 Maven 多模块或单模块项目结构
- 集成 SpringDoc OpenAPI 用于 API 文档自动生成
- 提供统一的 RESTful API 响应格式
- 配置 Lombok 简化代码

**Non-Goals:**
- 业务逻辑实现
- 数据库集成
- 认证授权机制
- 容器化部署配置

## Decisions

### 1. 项目结构
选择标准的 Spring Boot 单模块结构：
```
src/
├── main/
│   ├── java/com/example/toolbox/
│   │   ├── ToolboxApplication.java
│   │   ├── config/
│   │   ├── controller/
│   │   ├── model/
│   │   └── common/
│   └── resources/
│       └── application.yml
└── test/
```
**理由**: 简单项目无需多模块，保持结构清晰即可。

### 2. OpenAPI 集成
选择 SpringDoc OpenAPI (springdoc-openapi-starter-webmvc-ui) 而非 Swagger 2.x。
**理由**: SpringDoc 支持 Spring Boot 3.x 和 OpenAPI 3.0 规范，维护更活跃。

### 3. 统一响应格式
定义 `Result<T>` 泛型类作为标准响应：
```java
public class Result<T> {
    private int code;
    private String message;
    private T data;
}
```
**理由**: 提供一致的 API 响应结构，便于前端统一处理。

### 4. 异常处理
使用 `@ControllerAdvice` 实现全局异常处理。
**理由**: 集中管理异常，避免重复代码，统一错误响应格式。

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| 项目结构可能不适合复杂业务 | 保持结构可扩展，后续可拆分模块 |
| OpenAPI 版本兼容性 | 使用最新稳定版本，关注 springdoc 更新 |