package ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.cache;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.ClassUtils;

import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * Create HazelcastInstance from Hazelcast Config
 * <br> Load @HazelcastConfiguration beans and build config
 */
@Configuration
@Slf4j
public class HazelcastInstanceConfig {

    @Autowired
    private ApplicationContext context;

    @Autowired
    protected Config config;

    @Bean
    @Primary
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance(createConfig());
    }


    private Config createConfig() {

        // 1. Get all beans annotated with @HazelcastConfiguration
        //    Spring will call @PostConstruct on each of this bean
        Map<String,Object> beans = context.getBeansWithAnnotation(HazelcastConfiguration.class);

        // After this hazelcast config will be configured in all that beans,
        // and we could create HazelcastInstance

        // Avoiding altering Hazelcast Config after creating HazelcastInstance
        // If nodes have different configs this lead to glitches and cluster malfunctioning
        return config;
    }

}
