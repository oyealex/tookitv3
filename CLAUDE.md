# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在本项目中工作时提供指导。

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
├── repository/     # 数据访问 (JDBC)
├── model/          # 领域模型、DTO、枚举
│   └── dto/        # API 请求/响应的数据传输对象
├── config/         # Spring 配置类
├── common/         # 共享工具（异常、处理器）
└── util/           # 工具类
```

### 核心组件

- **REST API**：使用 `@RestController` 和统一的 `Result<T>` 响应格式
- **数据库**：通过 Spring JDBC 使用 SQLite（`data/toolbox.db`）
- **Excel 导入导出**：使用 Apache POI EasyExcel 处理设备数据
- **API 文档**：SpringDoc OpenAPI，Swagger UI 位于 `/swagger-ui.html`
- **国际化**：消息资源位于 `i18n/messages*.properties`

### OpenSpec 工作流

本项目使用自定义的 **OpenSpec** 基于制品的工作流来管理功能开发。主要命令：

- `/opsx:propose` - 创建包含提案、设计和任务的新变更
- `/opsx:apply` - 执行 OpenSpec 变更中的任务
- `/opsx:verify` - 验证实现是否匹配变更制品
- `/opsx:archive` - 归档已完成的变更
- `/opsx:explore` - 在创建变更前探索想法

变更存储在 `openspec/changes/<变更名称>/`，包含 `proposal.md`、`design.md` 和 `tasks.md` 等制品。

## 配置

- **端口**：8080（在 `application.yml` 中配置）
- **数据库**：`data/toolbox.db`（SQLite）
- **API 文档**：位于 `/v3/api-docs`
- **Swagger UI**：位于 `/swagger-ui.html`

## 必须遵循的规则
- 始终使用中文与用户交流
- 始终使用中文撰写文档
- 代码中的注释始终使用中文
- 代码和专业词汇保持使用英文
