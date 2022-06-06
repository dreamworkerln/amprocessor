package ru.kvanttelecom.tv.amprocessor.core.configurations.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonProperties {

    @Value("${client.protocol:http://}")
    @Getter
    private String protocol;

}
