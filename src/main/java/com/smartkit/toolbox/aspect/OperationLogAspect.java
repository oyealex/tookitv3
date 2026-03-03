package com.smartkit.toolbox.aspect;

import com.smartkit.toolbox.annotation.LogOperation;
import com.smartkit.toolbox.model.OperationLog;
import com.smartkit.toolbox.model.OperationResult;
import com.smartkit.toolbox.model.OperationType;
import com.smartkit.toolbox.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志 AOP 切面
 */
@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ExpressionParser parser = new SpelExpressionParser();

    public OperationLogAspect(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Around("@annotation(logOperation)")
    public Object around(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
        Object result = null;
        Throwable exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            // 构建日志上下文
            Map<String, Object> variables = buildVariables(joinPoint, result, exception);

            // 检查是否需要记录日志
            boolean shouldLog = shouldLog(logOperation, result, exception);
            if (!shouldLog) {
                return result;
            }

            // 提取日志信息
            OperationLog log = extractLogInfo(logOperation, variables);

            // 记录日志
            if (logOperation.async()) {
                operationLogService.logOperationAsync(log);
            } else {
                operationLogService.logOperation(log);
            }
        }
    }

    private Map<String, Object> buildVariables(ProceedingJoinPoint joinPoint,
                                               Object result, Throwable exception) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("result", result);
        variables.put("e", exception);
        variables.put("_this", joinPoint.getTarget());

        // 添加方法参数
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                variables.put(parameterNames[i], args[i]);
            }
        }

        // 添加 HttpServletRequest
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                variables.put("request", request);
            }
        } catch (Exception ignored) {
        }

        return variables;
    }

    private boolean shouldLog(LogOperation annotation, Object result, Throwable exception) {
        if (exception != null) {
            return annotation.logOnException();
        }
        return annotation.logOnSuccess();
    }

    private OperationLog extractLogInfo(LogOperation annotation, Map<String, Object> variables) {
        StandardEvaluationContext context = new StandardEvaluationContext(variables);

        OperationLog log = new OperationLog();

        // 提取 operationType
        log.setOperationType(annotation.operationType());

        // 提取 objectType
        log.setObjectType(annotation.objectType());

        // 提取 objectId
        if (!annotation.objectId().isEmpty()) {
            try {
                Object value = parser.parseExpression(annotation.objectId()).getValue(context);
                log.setObjectId(value != null ? value.toString() : null);
            } catch (Exception ignored) {
            }
        }

        // 提取 objectName
        if (!annotation.objectName().isEmpty()) {
            try {
                Object value = parser.parseExpression(annotation.objectName()).getValue(context);
                log.setObjectName(value != null ? value.toString() : null);
            } catch (Exception ignored) {
            }
        }

        // 提取 objectExtra
        if (!annotation.objectExtra().isEmpty()) {
            try {
                Object value = parser.parseExpression(annotation.objectExtra()).getValue(context);
                log.setObjectExtra(value != null ? value.toString() : null);
            } catch (Exception ignored) {
            }
        }

        // 提取 description
        if (!annotation.description().isEmpty()) {
            try {
                Object value = parser.parseExpression(annotation.description()).getValue(context);
                log.setDescription(value != null ? value.toString() : null);
            } catch (Exception ignored) {
            }
        }

        // 提取 result
        if (!annotation.result().isEmpty()) {
            try {
                Object value = parser.parseExpression(annotation.result()).getValue(context);
                if (value instanceof OperationResult) {
                    log.setResult((OperationResult) value);
                } else if (value instanceof String) {
                    log.setResult(OperationResult.valueOf((String) value));
                }
            } catch (Exception ignored) {
            }
        } else {
            // 默认成功
            log.setResult(OperationResult.SUCCESS);
        }

        // 提取 failureReason
        if (!annotation.failureReason().isEmpty() && variables.get("e") != null) {
            try {
                Object value = parser.parseExpression(annotation.failureReason()).getValue(context);
                log.setFailureReason(value != null ? value.toString() : null);
            } catch (Exception ignored) {
            }
        }

        // 提取 operator
        if (!annotation.operator().isEmpty()) {
            try {
                Object value = parser.parseExpression(annotation.operator()).getValue(context);
                log.setOperator(value != null ? value.toString() : null);
            } catch (Exception ignored) {
            }
        }

        // 提取 operatorIp
        if (annotation.operatorIp()) {
            try {
                HttpServletRequest request = (HttpServletRequest) variables.get("request");
                if (request != null) {
                    String ip = getClientIp(request);
                    log.setOperatorIp(ip);
                }
            } catch (Exception ignored) {
            }
        }

        // 设置操作时间
        log.setOperationTime(LocalDateTime.now());

        return log;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}