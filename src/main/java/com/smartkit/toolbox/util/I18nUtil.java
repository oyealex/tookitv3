package com.smartkit.toolbox.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class I18nUtil {

    private static MessageSource messageSource;

    public I18nUtil(MessageSource messageSource) {
        I18nUtil.messageSource = messageSource;
    }

    public static String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }
}