package ru.kvanttelecom.tv.amprocessor.configz.prometheus.configurations;

import com.github.anhdat.PrometheusApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.dreamworkerln.spring.utils.common.rest.RestClient;
import ru.dreamworkerln.spring.utils.common.rest.RestClientBuilder;
import ru.kvanttelecom.tv.amprocessor.configz.prometheus.configurations.properties.PrometheusProperties;

@Configuration
public class PrometheusBeanConfigurations {

    public static final String REST_TEMPLATE_PROMETHEUS = "rest_template_prometheus";
    
    @Autowired
    private PrometheusProperties promProps;

    @Bean
    PrometheusApiClient getPrometheusApiClient() {
        return new PrometheusApiClient(promProps.getProtocol() + promProps.getAddress(),
            promProps.getUsername(),
            promProps.getPassword());
    }

    @Bean(REST_TEMPLATE_PROMETHEUS)
    public RestClient restClientPrometheus(RestTemplate restTemplate) {

        RestClientBuilder builder = new RestClientBuilder();
        return builder
            .restTemplate(restTemplate)
            .basicAuth(promProps.getUsername(), promProps.getPassword())
            .build();
    }
}
