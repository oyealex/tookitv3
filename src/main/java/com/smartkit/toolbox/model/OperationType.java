package com.smartkit.toolbox.model;

/**
 * 操作类型枚举，定义系统支持的操作类型。
 *
 * @author SmartKit
 * @since 1.0.0
 */
public enum OperationType {
    /**
     * 创建操作
     */
    CREATE,

    /**
     * 更新操作
     */
    UPDATE,

    /**
     * 删除操作
     */
    DELETE,

    /**
     * 导入操作
     */
    IMPORT,

    /**
     * 导出操作
     */
    EXPORT,

    /**
     * 工具扫描
     */
    SCAN,

    /**
     * 工具执行
     */
    EXECUTE
}