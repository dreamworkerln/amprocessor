package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.broker.configurations;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.cache.HazelcastConfiguration;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.CacheConfiguration;

@HazelcastConfiguration
@Component
@Slf4j
public class AlertMessageBusConfiguration extends CacheConfiguration {


    @Override
    protected void setNames() {
        mapName           = "MAP_ALERTBUS";
        requestTopicName  = "TOPIC_ALERTBUS_NOT_USED";
        responseTopicName = "TOPIC_ALERTBUS_NEW_ALERT_ARRIVED";
    }
}
