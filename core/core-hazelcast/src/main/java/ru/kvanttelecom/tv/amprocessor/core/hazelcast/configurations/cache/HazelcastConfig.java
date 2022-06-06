package ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.cache;

import com.hazelcast.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Configuration
public class HazelcastConfig {

    @Bean
    @Primary
    public Config config() {
        return new Config();
    }
}
