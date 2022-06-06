package ru.kvanttelecom.tv.amprocessor.alerthandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.utils.startuprunner.BaseStartupRunner;

@Component
@Slf4j
public class AlertHandlerInitializer extends BaseStartupRunner {

//    @Autowired
//    Environment env;
//
//    @Override
//    public void run(ApplicationArguments args) {
//        super.run(args);
//
//        Properties props = new Properties();
//        MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
//        StreamSupport.stream(propSrcs.spliterator(), false)
//            .filter(ps -> ps instanceof EnumerablePropertySource)
//            .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
//            .flatMap(Arrays::stream)
//            .forEach(s -> System.out.println(s +": " + env.getProperty(s)));
//
//        /*propName -> props.setProperty(propName, springEnv.getProperty(propName))*/
//
//
//
//    }



}
