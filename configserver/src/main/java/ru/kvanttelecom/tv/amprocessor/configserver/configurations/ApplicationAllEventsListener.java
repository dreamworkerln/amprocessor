package ru.kvanttelecom.tv.amprocessor.configserver.configurations;

import lombok.SneakyThrows;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Will listen all events, even before Spring context is up (and no beans have been created)
 * Configured in spring.factories
 */
public class ApplicationAllEventsListener implements ApplicationListener<ApplicationEvent> {

    private boolean slept = false;
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //System.out.println("Application event: " + event);

//        if(!slept) {
//            System.out.println("Sleeping 3 sec");
//            TimeUnit.SECONDS.sleep(3);
//            System.out.println("Sleeping done ...");
//            slept = true;
//        }

    }
}
