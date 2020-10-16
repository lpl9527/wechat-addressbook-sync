package com.supwisdom.platform.framework.util;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;

public class ExceptionUtil {
    private static MessageSource messageSource;
    private static LocaleResolver localeResolver;
    public static final String DEFAULT_EXCEPTION_MESSAGE = "common.unknownException";

    public static String getMessage(String msg) {
        if (msg == null) {
            msg = DEFAULT_EXCEPTION_MESSAGE;
        }
        if (messageSource != null) {
            Locale locale = null;
            if (localeResolver != null) {
                HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
                locale = localeResolver.resolveLocale(req);
            }
            msg = messageSource.getMessage(msg, null, msg, locale);
        }
        return msg;
    }

    public static String getRealCode(String code) {
        StringBuilder builder = new StringBuilder();
        String[] clazz = Thread.currentThread().getStackTrace()[2].getClassName().split("\\.");
        String clazzName = clazz[clazz.length - 1];
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return builder.append(clazzName).append(".").append(methodName).append(".").append(code).toString();
    }

    public LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    public void setLocaleResolver(LocaleResolver localeResolver) {
        ExceptionUtil.localeResolver = localeResolver;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        ExceptionUtil.messageSource = messageSource;
    }
}
