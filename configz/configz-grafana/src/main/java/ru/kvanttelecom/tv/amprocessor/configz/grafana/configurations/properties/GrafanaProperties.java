package ru.kvanttelecom.tv.amprocessor.configz.grafana.configurations.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GrafanaProperties {

    @Value("${grafana.address:}")
    @Getter
    private String address;


//    @Value("${grafana.username}")
//    @Getter
//    private String username;
//
//
//    @Value("${grafana.password}")
//    @Getter
//    private String password;


    @Value("${grafana.stream-view-path:}")
    @Getter
    private String streamViewQuery;




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
