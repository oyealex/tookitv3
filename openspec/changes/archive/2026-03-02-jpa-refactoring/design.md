## Context

项目当前使用Spring JDBC的JdbcTemplate进行数据库操作，存在以下问题：
- 大量手写的SQL语句，重复代码多
- 手动ResultSet到对象的映射，繁琐且易出错
- 缺乏编译时类型检查
- 查询方法无法在编译时验证

目标是将数据访问层重构为使用Spring Data JPA，同时：
1. **不修改任何业务逻辑** - Service层、Controller层保持完全不变
2. **使用TDD模式** - 先编写测试用例确保行为不变，再进行重构
3. **保持SQLite兼容性** - 项目使用SQLite，需要确保JPA能够正常工作

## Goals / Non-Goals

**Goals:**
- 引入Spring Data JPA，实现标准化Repository接口
- 添加JPA实体类，替代手动的RowMapper映射
- 配置Hibernate与SQLite的兼容集成
- 保持所有现有的API端点行为完全一致
- 通过TDD确保重构过程中不破坏现有功能

**Non-Goals:**
- 不修改业务逻辑层代码
- 不修改Controller层
- 不修改现有的数据模型（Model/DTO）
- 不添加新的业务功能
- 不迁移到其他数据库

## Decisions

### 1. JPA Provider选择
**决定使用**: Hibernate 7.0 (hibernate-community-bundles)

**备选方案**:
- EclipseLink: 功能完整但与SQLite集成不如Hibernate成熟
- MyBatis: 保留SQL控制但失去JPA的ORM优势

**理由**: Hibernate是Spring Data JPA的默认实现，社区版本对SQLite有较好的支持。

### 2. SQLite JPA配置策略
**决定使用**: Hibernate ORM 7.0 + Hibernate Community SQLite Dialect

**备选方案**:
- 保持JdbcTemplate + 手动SQL: 不符合重构目标
- 使用Hibernate的native支持: 仍需dialect

**理由**: hibernate-community-dialects提供正式的SQLite方言支持。

### 3. TDD重构流程
1. 首先编写Repository接口的测试用例（使用@DataJpaTest或集成测试）
2. 运行测试，确保测试失败（因为没有JPA实现）
3. 创建JPA实体类和JpaRepository接口
4. 实现自定义查询方法
5. 运行测试验证行为一致
6. 逐步替换Service中的Repository注入
7. 保留旧的JdbcTemplate Repository作为备份（可选）

### 3.1 串行重构策略
为降低风险，采用串行重构策略：
- 先完成DeviceRepository的完整重构和测试
- 再开始OperationLogRepository的重构
- 每次切换Repository后运行完整测试套件

### 4. 实体映射策略
**决定使用**: 独立于现有Model的新Entity类

**理由**:
- 保持现有Model的纯粹性（不含JPA注解）
- 可以在Entity层添加JPA特定优化（如@BatchSize）
- 解耦数据访问层和业务层

## Risks / Trade-offs

### Risk 1: SQLite与JPA兼容性
**描述**: SQLite对某些JPA特性支持有限（如延迟加载、级联操作）

**缓解**:
- 使用FetchType.EAGER明确指定加载策略
- 避免复杂的级联操作
- 保留原有的查询逻辑风格

### Risk 2: 重构过程破坏现有功能
**描述**: 重构过程中可能引入bug导致业务功能异常

**缓解**:
- 严格按照TDD流程：先写测试，再写实现
- 每次修改后运行完整测试套件
- 保留旧的Repository实现作为快速回滚方案

### Risk 3: 性能下降
**描述**: JPA/Hibernate可能引入性能开销

**缓解**:
- 使用Spring Data JPA的方法命名查询，避免动态SQL
- 适当使用@Query注解优化复杂查询
- 监控并对比重构前后的性能指标

### Trade-off 1: 代码量增加
JPA需要额外的Entity类，初期代码量可能增加，但长期维护性更好。

### Trade-off 2: 学习成本
团队需要熟悉JPA/Hibernate的使用，但这是Spring生态的标准技能。

## Migration Plan

1. **Phase 1: 准备**
   - 添加Maven依赖
   - 配置application.yml

2. **Phase 2: 实体开发**
   - 创建DeviceEntity
   - 创建OperationLogEntity

3. **Phase 3: Repository重构（Device）**
   - 编写DeviceRepository测试用例
   - 创建DeviceJpaRepository
   - 验证测试通过

4. **Phase 4: Repository重构（OperationLog）**
   - 编写OperationLogRepository测试用例
   - 创建OperationLogJpaRepository
   - 验证测试通过

5. **Phase 5: Service层切换**
   - 修改ServiceImpl中的Repository注入
   - 运行完整测试套件

6. **Phase 6: 清理**
   - 移除旧的JdbcTemplate Repository
   - 验证所有功能正常