## Why

需要一个基础的 Spring Boot 项目模板，用于快速构建 RESTful API 服务。该模板将集成 OpenAPI 规范，提供标准化的 API 文档生成能力，简化后续项目的初始化工作。

## What Changes

- 创建一个新的 Spring Boot 空项目，使用 Java 21 和 Maven 构建
- 配置 RESTful API 基础结构
- 集成 OpenAPI (SpringDoc) 用于 API 文档自动生成
- 配置 Lombok 简化代码

## Capabilities

### New Capabilities

- `project-structure`: 项目基础结构和 Maven 配置，包含必要的依赖管理
- `rest-api`: RESTful API 基础框架，包含统一的响应格式和异常处理
- `openapi-docs`: OpenAPI 文档自动生成，提供 Swagger UI 界面

### Modified Capabilities

无（全新项目）

## Impact

- 新增项目目录结构
- 新增 Maven 配置文件 `pom.xml`
- 新增 Spring Boot 主应用类和配置文件
- 新增 OpenAPI 配置