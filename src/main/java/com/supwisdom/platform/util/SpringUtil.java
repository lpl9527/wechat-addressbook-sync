package com.supwisdom.platform.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    /**
     * 获取ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.getApplicationContext() == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    //通过实体名称获取Bean
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }
}
