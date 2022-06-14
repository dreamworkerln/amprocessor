package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.firing.configurations;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.cache.HazelcastConfiguration;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.CacheConfiguration;

@HazelcastConfiguration
@Component
@Slf4j
public class FiringAlertCacheConfiguration extends CacheConfiguration {


    @Override
    protected void setNames() {
        mapName           = "MAP_FIRINGALERTS";
        requestTopicName  = "TOPIC_FIRINGALERTS_STATE_CHANGED_REQUEST";
        responseTopicName = "TOPIC_FIRINGALERTS_STATE_CHANGED_RESPONSE";
    }
}
