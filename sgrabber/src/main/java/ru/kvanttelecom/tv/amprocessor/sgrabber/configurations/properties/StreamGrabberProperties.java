package ru.kvanttelecom.tv.amprocessor.sgrabber.configurations.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.configz.watcher.configurations.properties.WatcherProperties;
import ru.kvanttelecom.tv.amprocessor.core.configurations.properties.CommonProperties;

import javax.annotation.PostConstruct;

@Component
public class StreamGrabberProperties {

    @Getter
    @Autowired
    private WatcherProperties watcher;

    @Getter
    @Autowired
    private CommonProperties commonProps;

    @Getter
    private String protocol;

    @PostConstruct
    private void init() {
        protocol = commonProps.getProtocol();
    }
}
