## 1. 环境准备

- [ ] 1.1 添加Maven依赖（spring-boot-starter-data-jpa、hibernate-community-dialects）
- [ ] 1.2 配置application.yml中的JPA相关设置（database platform、show-sql等）
- [ ] 1.3 配置Hibernate的ddl-auto为validate（验证schema匹配）
- [ ] 1.4 验证项目可以正常启动

## 2. Device实体开发

- [ ] 2.1 创建DeviceEntity实体类，映射device表
- [ ] 2.2 配置Entity与现有Model的字段映射关系
- [ ] 2.3 添加@Table和@Column注解
- [ ] 2.4 创建DeviceMapper（Entity ↔ Model转换器）

## 3. OperationLog实体开发

- [ ] 3.1 创建OperationLogEntity实体类，映射operation_log表
- [ ] 3.2 配置枚举类型字段的转换（@Enumerated）
- [ ] 3.3 添加@Table和@Column注解
- [ ] 3.4 创建OperationLogMapper（Entity ↔ Model转换器）

## 4. DeviceRepository重构（TDD模式）

- [ ] 4.1 编写DeviceRepository测试用例（验证CRUD操作）
- [ ] 4.2 创建DeviceJpaRepository接口，继承JpaRepository
- [ ] 4.3 实现自定义查询方法（findAll带条件、分页）
- [ ] 4.4 运行测试验证功能正确性

## 5. OperationLogRepository重构（TDD模式）

- [ ] 5.1 编写OperationLogRepository测试用例（验证CRUD操作）
- [ ] 5.2 创建OperationLogJpaRepository接口，继承JpaRepository
- [ ] 5.3 实现自定义查询方法（findAll带条件、分页、count）
- [ ] 5.4 运行测试验证功能正确性

## 6. Service层切换

- [ ] 6.1 修改DeviceServiceImpl，注入新的JpaRepository
- [ ] 6.2 修改OperationLogService，注入新的JpaRepository
- [ ] 6.3 运行DeviceService相关测试，确保功能正常
- [ ] 6.4 运行OperationLogService相关测试，确保功能正常

## 7. 清理与验证

- [x] 7.1 运行完整测试套件（mvn test）
- [x] 7.2 手动测试关键API端点
- [x] 7.3 移除旧的JdbcTemplate Repository类（已用JPA实现替代）
- [x] 7.4 更新pom.xml，移除不再需要的依赖（可选）
- [x] 7.5 清理代码，移除无用注释

## 8. 文档与收尾

- [ ] 8.1 更新README或开发文档（可选）
- [ ] 8.2 标记变更完成，进行代码审查