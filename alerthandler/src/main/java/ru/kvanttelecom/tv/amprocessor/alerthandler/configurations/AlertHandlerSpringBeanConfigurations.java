package ru.kvanttelecom.tv.amprocessor.alerthandler.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kvanttelecom.tv.amprocessor.configz.prometheus.configurations.properties.PrometheusProperties;
import ru.kvanttelecom.tv.amprocessor.core.amqp.services.alert.rpc.AmqpRpcServer;

@Configuration
public class AlertHandlerSpringBeanConfigurations {

    @Autowired
    ApplicationContext context;

    @Autowired
    PrometheusProperties promProps;

    /**
     * Start Amqp rpc server
     */
    @Bean
    public AmqpRpcServer getAmqpRpcServer(ApplicationContext context) {
        return new AmqpRpcServer(context);
    }

    // Prometheus client


}
