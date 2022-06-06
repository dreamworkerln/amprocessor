package ru.kvanttelecom.tv.amprocessor.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Static autowire ..... костыли
 */
@Component
public class SpringContextConfig implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * Returns the Spring managed bean instance of the given class type if it exists.
     * Returns null otherwise.
     * @param beanClass bean class
     * @return bean
     */


    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return context.getBean(requiredType);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {

        // store ApplicationContext reference to access required beans later on
        SpringContextConfig.context = context;
    }

}