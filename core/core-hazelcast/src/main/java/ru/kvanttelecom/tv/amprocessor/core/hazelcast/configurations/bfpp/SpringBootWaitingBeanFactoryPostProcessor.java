package ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.bfpp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class SpringBootWaitingBeanFactoryPostProcessor implements BeanFactoryPostProcessor {


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {

        String initLevelCheckerBeanName = SpringBootDependenciesAvailableChecker.class.getSimpleName();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(SpringBootDependenciesAvailableChecker.class);
        ((DefaultListableBeanFactory) bf).registerBeanDefinition(initLevelCheckerBeanName, bd);

        // Make beans that needed to be suspended depends on the SpringBootReadinessChecker
        List<String> classes = List.of(
            "org.springframework.web.servlet.DispatcherServlet",
            "org.springframework.boot.task.TaskSchedulerBuilder");

        List<Class<?>> dependents = new ArrayList<>();

        classes.forEach(s -> {
            try {
                dependents.add(Class.forName(s));
            }
            catch (Throwable ignore) {}
        });
        dependents.forEach(c-> setBeanDefinitionDependsOn(initLevelCheckerBeanName, c, bf));
    }

    private void setBeanDefinitionDependsOn(String injectingBeanName,
                                            Class<?> dependentBeanClass,
                                            ConfigurableListableBeanFactory bf) {


        try {
            String[] beanNames = bf.getBeanNamesForType(dependentBeanClass);
            Stream.of(beanNames)
                .map(bf::getBeanDefinition)
                .forEach(it -> it.setDependsOn(injectingBeanName));
        }
        catch (Exception skip) {
            log.debug("Bean definition '{}' not found", dependentBeanClass);
        }
    }
}
