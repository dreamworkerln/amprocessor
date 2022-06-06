package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.configurations;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.cache.HazelcastConfiguration;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.CacheConfiguration;

@HazelcastConfiguration
@Component
@Slf4j
public class CamerasCacheConfiguration extends CacheConfiguration {


    @Override
    protected void setNames() {
        mapName           = "MAP_CAMERAS";
        requestTopicName  = "TOPIC_CAMERAS_STATE_CHANGED_REQUEST";
        responseTopicName = "TOPIC_CAMERAS_STATE_CHANGED_RESPONSE";
    }
}
