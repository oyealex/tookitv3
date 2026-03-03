package com.smartkit.toolbox.annotation;

import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标记在方法上自动记录操作日志
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {

    /**
     * 操作类型
     */
    OperationType operationType();

    /**
     * 操作对象类型（如 DEVICE）
     */
    String objectType() default "DEVICE";

    /**
     * 操作对象ID，支持 SpEL 表达式
     * 例如: "#device.ip" 或 "#result.id"
     */
    String objectId() default "";

    /**
     * 操作对象名称，支持 SpEL 表达式
     * 例如: "#device.name"
     */
    String objectName() default "";

    /**
     * 操作对象额外信息（JSON），支持 SpEL 表达式
     */
    String objectExtra() default "";

    /**
     * 操作描述，支持 SpEL 表达式
     * 例如: "'创建设备: ' + #device.name"
     */
    String description() default "";

    /**
     * 操作结果，支持 SpEL 表达式
     * 例如: "#result" 或 "'SUCCESS'"
     */
    String result() default "T(com.smartkit.toolbox.model.OperationResult).SUCCESS";

    /**
     * 失败原因，支持 SpEL 表达式
     * 例如: "#e.message"
     */
    String failureReason() default "";

    /**
     * 操作人
     */
    String operator() default "";

    /**
     * 操作者 IP，从 HttpServletRequest 获取
     */
    boolean operatorIp() default false;

    /**
     * 是否异步记录
     */
    boolean async() default true;

    /**
     * 是否在方法执行成功后才记录
     */
    boolean logOnSuccess() default true;

    /**
     * 是否在方法抛出异常时记录
     */
    boolean logOnException() default true;
}