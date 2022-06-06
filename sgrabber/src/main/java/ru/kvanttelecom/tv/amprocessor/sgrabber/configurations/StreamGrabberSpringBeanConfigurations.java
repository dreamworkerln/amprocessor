package ru.kvanttelecom.tv.amprocessor.sgrabber.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ru.kvanttelecom.tv.amprocessor.sgrabber.configurations.properties.StreamGrabberProperties;


@Configuration
public class StreamGrabberSpringBeanConfigurations {

    @Autowired
    StreamGrabberProperties props;

//    private final int WATCHER_HTTP_TIMEOUT = 20000;
//    private static final String REST_TEMPLATE_WATCHER   = "rest_template_watcher";
//    public static final String REST_CLIENT_MEDIASERVER = "rest_client_mediaserver";
//    public static final String REST_CLIENT_WATCHER     = "rest_client_watcher";
    //public static final String WATCHER_ORGANIZATIONS     = "watcher_organizations";




    // Rest MediaServer


//
//    @Bean(REST_CLIENT_MEDIASERVER)
//    public RestClient restClientMediaServer(RestTemplate restTemplate) {
//
//        RestClientBuilder builder = new RestClientBuilder();
//        return builder
//            .restTemplate(restTemplate)
//            .basicAuth(props.getMediaServers().getUsername(), props.getMediaServers().getPassword())
//            .build();
//    }
//
//    @Bean(REST_CLIENT_WATCHER)
//    public RestClient restClientWatcher(@Qualifier(REST_TEMPLATE_WATCHER) RestTemplate restTemplate) {
//
//        RestClientBuilder builder = new RestClientBuilder();
//        return builder
//            .restTemplate(restTemplate)
//            .header("x-vsaas-session", props.getWatcher().getToken())
//            .userAgent("PostmanRuntime/7.26.10")
//            .acceptEncoding("gzip, deflate, br")
//            .build();
//    }
//
//
//
//    // Rest Watcher
//
//    @Bean(REST_TEMPLATE_WATCHER)
//    public RestTemplate restTemplate(RestTemplateBuilder builder) {
//        return
//            builder.setConnectTimeout(Duration.ofMillis(WATCHER_HTTP_TIMEOUT))
//                .setReadTimeout(Duration.ofMillis(WATCHER_HTTP_TIMEOUT))
//                .build();
//    }



}

//
//    // lame bean definition
//    @Bean(WATCHER_ORGANIZATIONS)
//    public Map<Long, String> getOrganizations() {
//        return new HashMap<>();
//    }
