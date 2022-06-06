package ru.kvanttelecom.tv.amprocessor.configz.watcher.configurations.properties;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WatcherProperties {

    /**
     * Address of watcher (host:port)
     */
    @Getter(AccessLevel.PUBLIC)
    @Value("${watcher.address}")
    private String address;


    @Value("${watcher.username}")
    @Getter
    private String username;


    @Value("${watcher.password}")
    @Getter
    private String password;

    @Value("${watcher.token}")
    @Getter
    private String token;

    @Value("${watcher.stream-view-path:}")
    @Getter
    private String cameraViewPath;
}
