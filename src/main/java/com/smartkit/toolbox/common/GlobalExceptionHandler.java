package com.smartkit.toolbox.common;

import com.smartkit.toolbox.model.Result;
import com.smartkit.toolbox.model.ResultCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 全局异常处理器，统一处理系统中的异常并返回统一的响应格式。
 *
 * @author SmartKit
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 消息源，用于国际化
     */
    private final MessageSource messageSource;

    /**
     * 构造方法，注入依赖
     *
     * @param messageSource 消息源
     */
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 错误响应结果
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（RequestBody）
     *
     * @param e 参数校验异常
     * @return 错误响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> {
                String msg;
                try {
                    msg = messageSource.getMessage(error.getDefaultMessage(), null, locale);
                } catch (Exception ex) {
                    msg = error.getDefaultMessage();
                }
                return error.getField() + ": " + msg;
            })
            .collect(Collectors.joining("; "));
        return Result.error(400, message);
    }

    /**
     * 处理约束违反异常（单个参数校验）
     *
     * @param e 约束违反异常
     * @return 错误响应结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = e.getConstraintViolations().stream()
            .map(violation -> {
                String msg;
                try {
                    msg = messageSource.getMessage(violation.getMessage(), null, locale);
                } catch (Exception ex) {
                    msg = violation.getMessage();
                }
                return msg;
            })
            .collect(Collectors.joining("; "));
        return Result.error(400, message);
    }

    /**
     * 处理缺少请求参数异常
     *
     * @param e 缺少请求参数异常
     * @return 错误响应结果
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理非法参数异常
     *
     * @param e 非法参数异常
     * @return 错误响应结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理消息不存在异常
     *
     * @param e 消息不存在异常
     * @return 错误响应结果
     */
    @ExceptionHandler(NoSuchMessageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleNoSuchMessageException(NoSuchMessageException e) {
        return Result.error(500, "Message not found: " + e.getMessage());
    }

    /**
     * 处理通用异常
     *
     * @param e 异常对象
     * @return 错误响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.error(ResultCode.INTERNAL_ERROR);
    }

}