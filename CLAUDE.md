# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于 **Spring Boot 3.3.0** 的 REST API 应用，使用 **Java 21** 和 **Maven** 构建。项目提供设备管理功能，支持 SQLite 数据库和 Excel 导入导出。

## 常用命令

```bash
# 构建项目
mvn clean package

# 运行应用程序
mvn spring-boot:run

# 运行测试
mvn test

# 运行单个测试类
mvn test -Dtest=DeviceControllerTest

# 运行测试并显示详细输出
mvn test -Dsurefire.useFile=false

# 跳过测试构建
mvn clean package -DskipTests

# 验证 Maven 配置
mvn validate
```

## 架构

项目遵循标准 Spring Boot 分层架构：

```
src/main/java/com/smartkit/toolbox/
├── controller/     # REST API 端点 (@RestController)
├── service/        # 业务逻辑 (@Service)
│   └── scenario/   # 场景模块服务
├── repository/     # 数据访问 (JPA Repository)
├── entity/         # JPA 实体类
├── model/          # 领域模型、DTO、枚举
│   ├── dto/        # API 请求/响应的数据传输对象
│   ├── tool/       # 工具相关模型
│   └── scenario/   # 场景相关模型
├── config/         # Spring 配置类
├── common/         # 共享工具（异常、处理器、常量）
├── util/           # 工具类
├── aspect/         # AOP 切面（操作日志）
├── annotation/     # 自定义注解
├── scanner/        # 工具扫描器
│   └── impl/       # 扫描器实现
├── invoker/        # 工具执行器
│   └── impl/       # 执行器实现
├── manager/        # 管理器（ToolManager）
├── monitor/        # 监控接口
│   └── impl/       # 监控实现
└── lock/           # 工具锁管理
```

### 核心组件

- **REST API**：使用 `@RestController` 和统一的 `Result<T>` 响应格式
- **数据库**：JPA + SQLite（`data/toolbox.db`），使用 Hibernate SQLiteDialect
- **Excel 导入导出**：使用 Alibaba EasyExcel 处理设备数据
- **API 文档**：SpringDoc OpenAPI，Swagger UI 位于 `/swagger-ui.html`
- **国际化**：消息资源位于 `i18n/messages*.properties`
- **操作日志**：通过 AOP 切面自动记录 API 调用

### 子工具模块 (tools/)

项目支持动态加载子工具，工具配置位于 `tools/` 目录，包含 `tool.json` 配置文件。

### 场景模块

支持场景编排功能，场景定义存储在 `scenarios/` 目录。

### OpenSpec 工作流

本项目使用自定义的 **OpenSpec** 基于制品的工作流来管理功能开发。主要命令：

- `/opsx:propose` - 创建包含提案、设计和任务的新变更
- `/opsx:apply` - 执行 OpenSpec 变更中的任务
- `/opsx:verify` - 验证实现是否匹配变更制品
- `/opsx:archive` - 归档已完成的变更
- `/opsx:explore` - 在创建变更前探索想法

变更存储在 `openspec/changes/<变更名称>/`，包含 `proposal.md`、`design.md` 和 `tasks.md` 等制品。已完成的变更归档在 `openspec/changes/archive/`。

## 配置

- **端口**：8081（在 `application.yml` 中配置）
- **数据库**：`data/toolbox.db`（SQLite）
- **API 文档**：位于 `/v3/api-docs`
- **Swagger UI**：位于 `/swagger-ui.html`

## 必须遵循的规则

- 始终使用中文与用户交流
- 始终使用中文撰写文档
- 代码中的注释始终使用中文
- 代码和专业词汇保持使用英文
- 生成的Java代码的所有public、protected、package级别的类、方法和字段必须添加JavaDoc
- 日志内容必须使用英文
