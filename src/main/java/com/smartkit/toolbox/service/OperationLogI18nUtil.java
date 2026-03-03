package com.smartkit.toolbox.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 操作日志国际化工具类
 */
@Component
public class OperationLogI18nUtil {

    private final MessageSource messageSource;

    public OperationLogI18nUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 获取本地化的消息
     * 如果 description 是消息键（以 operation. 开头），则解析为对应语言
     * 否则直接返回原始文本
     */
    public String getLocalizedMessage(String description) {
        if (description == null) {
            return null;
        }

        // 如果是消息键，尝试解析
        if (description.startsWith("operation.")) {
            try {
                return messageSource.getMessage(description, null, description, LocaleContextHolder.getLocale());
            } catch (Exception e) {
                // 解析失败，返回原始文本
                return description;
            }
        }

        return description;
    }

    /**
     * 获取当前语言环境
     */
    public Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }
}