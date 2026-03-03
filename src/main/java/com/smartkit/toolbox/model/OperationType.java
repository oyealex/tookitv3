package com.smartkit.toolbox.model;

/**
 * 操作类型枚举
 */
public enum OperationType {
    CREATE,   // 创建操作
    UPDATE,   // 更新操作
    DELETE,   // 删除操作
    IMPORT,   // 导入操作
    EXPORT,   // 导出操作
    SCAN,     // 工具扫描
    EXECUTE   // 工具执行
}