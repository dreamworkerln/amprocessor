package ru.kvanttelecom.tv.amprocessor.utils.beans;

import org.springframework.aop.framework.AopProxyUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class BeanUtilsMy {

    /**
     * Filter input bean list, keep beans only with specified annotation
     * @param beans input beans
     * @param annotation shsould have this annotation
     * @return filtered bean list
     */
    public static <T> List<T> filter(List<T> beans, Class<? extends Annotation> annotation) {

        List<T> result = new ArrayList<>();

        for (T bean : beans) {
            Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);
            Annotation beanAnnotation = beanClass.getAnnotation(annotation);

            if(beanAnnotation != null) {
                result.add(bean);
            }
        }
        return result;
    }


    public static boolean haveAnnotation(Object bean, Class<? extends Annotation> annotation) {

        boolean result = false;

        Class<?> beanClass =  AopProxyUtils.ultimateTargetClass(bean);
        Annotation beanAnnotation = beanClass.getAnnotation(annotation);
        if(beanAnnotation != null) {
            result = true;
        }

        return result;
    }


//        // bean is an AOP proxy
//        if (beanAnnotation == null) {
//            beanClass = AopProxyUtils.ultimateTargetClass(bean);
//            beanAnnotation = beanClass.getAnnotation(annotation);
//        }


    public static <T extends Annotation> T getAnnotation(Object bean, Class<T> annotationClass) {

        T result;

        Class<?> beanClass =  AopProxyUtils.ultimateTargetClass(bean);
        result = beanClass.getAnnotation(annotationClass);
        return result;
    }
}
