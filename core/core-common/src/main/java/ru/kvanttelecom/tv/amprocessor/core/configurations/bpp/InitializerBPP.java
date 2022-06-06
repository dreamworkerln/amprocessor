package ru.kvanttelecom.tv.amprocessor.core.configurations.bpp;

import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;
import ru.kvanttelecom.tv.amprocessor.core.annotations.Initializer;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * This BeanPostProcessor call method initialize()
 * for beans designated with @Initializer annotation
 * after bean has been completely created and configured
 */
@Slf4j
public class InitializerBPP implements BeanPostProcessor, Ordered {

    //@Autowired
    //ApplicationContext context;

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, @Nonnull String beanName) {

        //SimpleAnnotationMetadataReadingVisitor

        Set<Class<? extends Annotation>> filteredAnnotations =
            Sets.newHashSet(Documented.class, Retention.class, Target.class);

        //System.out.println(bean);


//        // Включая суперклассы
//        List<Class<?>> superClasses = new ArrayList<>();
//        for (Class<?> c = bean.getClass(); c != null; c = c.getSuperclass()) {
//            superClasses.add(c);
//        }

        // Proxy fight
//        Class<?> clazz = bean.getClass();
//        System.out.println("CLAZZ: " + clazz);
//        clazz = AopProxyUtils.ultimateTargetClass(bean);
//        System.out.println("CLAZZ: " + clazz);
//        if(bean instanceof TargetClassAware) {
//            clazz = ((TargetClassAware)bean).getTargetClass();
//            System.out.println("CLAZZ: " + clazz);
//        }

        // final proxy fight solution
        Class<?> clazz = ClassUtils.getUserClass(bean);

        //System.out.println("CLAZZ: " + clazz);

        //for (Class<?> clazz : superClasses) {

            //Initializer initializer = clazz.getAnnotation(Initializer.class);


            // Рекурсивно обходим все аннотации класса (включая аннотации аннотаций ...)
            //
            // Засовываем все аннотации с класса в стек aDeq
            // Далее будем итеративно проходить по аннотациям аннотации,
            // добавляя новые аннотации в стек aDeq и в aMap,
            // пока в aMap не соберутся все аннотации с класса.
            // Включена защита от циклов графа аннотаций - removeIf(filteredAnnotations)
            // а то будет обходить до бесконечности
            Map<Class<? extends Annotation>, Annotation> aMap = new HashMap<>();
            Deque<Annotation> aDeq = new ArrayDeque<>(Arrays.asList(clazz.getDeclaredAnnotations()));

            while(aDeq.size() > 0) {

                // вытаскиваем аннотацию
                Annotation a = aDeq.pop();

                // сохраняем в aMap
                aMap.put(a.annotationType(), a);

                // получаем все аннотации a
                Map<Class<? extends Annotation>, Annotation> tmp  = Arrays.stream(a.annotationType().getAnnotations())
                    .collect(Collectors.toMap(Annotation::annotationType, Function.identity()));

                // удаляем из tmp Documented и Retention
                tmp.entrySet().removeIf(e ->  filteredAnnotations.contains(e.getKey()));

                // помещаем отфильтрованные аннотации обратно в aDeq
                aDeq.addAll(tmp.values());
            }

            // Если нашли аннотацию Initializer, то ищем в классе initialize метод
            if (aMap.containsKey(Initializer.class)) {

                // Call initializer.initialize
                Method method;
                try {

                    Method[] methods = bean.getClass().getDeclaredMethods();
                    for(Method m : methods) {

                        if(m.getName().equals("initialize")) {
                            Class<?>[] params = m.getParameterTypes();
                            method = bean.getClass().getDeclaredMethod("initialize", params);
                            method.setAccessible(true);
                            method.invoke(bean, null);
                        }
                    }
                }
//                catch (NoSuchMethodException rethrow) {
//                    //Bean annotated @Initializer should have initialize() method
//                    log.error("", rethrow);
//                    throw rethrow;
//                }
                catch (Exception rethrow) {
                    log.error("Reflection run method error: ", rethrow);
                    throw rethrow;
                }

            }

        //}
        return bean;

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
