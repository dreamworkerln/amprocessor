package ru.kvanttelecom.tv.amprocessor.configz.watcher.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.dreamworkerln.spring.utils.common.rest.RestClientBuilder;
import ru.kvanttelecom.tv.amprocessor.configz.watcher.configurations.properties.WatcherProperties;


import java.time.Duration;


@Configuration
public class WatcherSpringBeanConfigurations {

    public static final int WATCHER_HTTP_TIMEOUT = 20000;
    public static final String REST_TEMPLATE_WATCHER   = "rest_template_watcher";
    public static final String REST_CLIENT_WATCHER     = "rest_client_watcher";
    //public static final String WATCHER_ORGANIZATIONS     = "watcher_organizations";
    //public static final String REST_CLIENT_MEDIASERVER = "rest_client_mediaserver";


    @Autowired
    private WatcherProperties watcherProperties;


//    // Rest MediaServer
//    @Bean(REST_CLIENT_MEDIASERVER)
//    public RestClient restClientMediaServer(RestTemplate restTemplate) {
//
//        RestClientBuilder builder = new RestClientBuilder();
//        return builder
//            .restTemplate(restTemplate)
//            .basicAuth(mediaServersProperties.getUsername(), mediaServersProperties.getPassword())
//            .build();
//    }

    @Bean(REST_CLIENT_WATCHER)
    public RestClient restClientWatcher(@Qualifier(REST_TEMPLATE_WATCHER) RestTemplate restTemplate) {

        RestClientBuilder builder = new RestClientBuilder();
        return builder
            .restTemplate(restTemplate)
            .header("x-vsaas-session", watcherProperties.getToken())
            .header("Content-Type", "application/json")
            .userAgent("Zrbite 0.1")
            .acceptEncoding("gzip, deflate, br")
            .build();
    }


    // ----------------------------------------------------------------------------

    // Rest Watcher
    @Bean(REST_TEMPLATE_WATCHER)
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return
            builder.setConnectTimeout(Duration.ofMillis(WATCHER_HTTP_TIMEOUT))
                .setReadTimeout(Duration.ofMillis(WATCHER_HTTP_TIMEOUT))
                .build();
    }



}

//
//    // lame bean definition
//    @Bean(WATCHER_ORGANIZATIONS)
//    public Map<Long, String> getOrganizations() {
//        return new HashMap<>();
//    }
