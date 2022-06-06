package ru.kvanttelecom.tv.amprocessor.sgrabber;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.utils.startuprunner.BaseStartupRunner;

@Component
@Slf4j
public class StreamGrabberInitializer extends BaseStartupRunner {

    @EventListener
    public void all(ApplicationEvent event) {
        //log.debug("{}", event);
    }

    @EventListener
    public void handleContextRefresh(ApplicationStartedEvent event) {
        //log.debug("{}", event);
    }

    @Override
    public void run(ApplicationArguments args) {
        super.run(args);
    }

}




//        List<Predicate<String>> predicates = Arrays.asList(s -> s.startsWith("A"), s -> s.endsWith("Z"));
//
//        Predicate<String> one =
//            predicates.stream()
//                .reduce(Predicate::and) // all true
//                .orElse(p -> false);
//
//        System.out.println(one.test("ABC"));
//        System.out.println(one.test("ABZ"));
//        System.out.println(one.test(""));
