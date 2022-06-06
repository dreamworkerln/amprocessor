package ru.kvanttelecom.tv.amprocessor.configserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringAllEventsListener {

    @EventListener
    public void handleAllEvents(ApplicationEvent event) {
        //log.debug("Spring event: " + event);
    }
}
