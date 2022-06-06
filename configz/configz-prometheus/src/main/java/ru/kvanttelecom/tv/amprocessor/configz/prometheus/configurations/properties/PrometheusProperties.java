package ru.kvanttelecom.tv.amprocessor.configz.prometheus.configurations.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.configurations.properties.CommonProperties;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Component
public class PrometheusProperties {

    public static String REST_API_GET_ALERTS_PATH = "/api/v1/alerts";

    @Autowired
    CommonProperties commonProps;


    @Value("${prometheus.address}")
    @Getter
    private String address;


    @Value("${prometheus.username}")
    @Getter
    private String username;


    @Value("${prometheus.password}")
    @Getter
    private String password;

    @Getter
    private String protocol;

    @PostConstruct
    private void init() {
        protocol = commonProps.getProtocol();
    }



//    @Autowired
//    @Getter
//    private PrometheusProperties.Webhook webhook;

    // Below properties disabled by reason of Debugging

    @Value("${alert.evaluate.interval.sec:#{T(java.time.Duration).of(10, T(java.time.temporal.ChronoUnit).SECONDS)}}")
    @Getter
    private Duration evaluateDuration;

    @Value("${alert.for.interval.sec:#{T(java.time.Duration).of(10, T(java.time.temporal.ChronoUnit).SECONDS)}}")
    @Getter
    private Duration forDuration;

//    @Component
//    public static class Webhook {
//        @Value("${prometheus.webhook.username:}")
//        @Getter
//        private String username;
//
//
//        @Value("${prometheus.webhook.password:}")
//        @Getter
//        private String password;
//    }

}
