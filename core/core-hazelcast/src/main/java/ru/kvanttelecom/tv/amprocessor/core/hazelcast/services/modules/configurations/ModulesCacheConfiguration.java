package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.configurations;

import com.hazelcast.config.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.cache.HazelcastConfiguration;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.CacheConfiguration;

import javax.annotation.PostConstruct;

@HazelcastConfiguration
@Component
@Slf4j
public class ModulesCacheConfiguration extends CacheConfiguration {

    @Getter
    private String memberModuleMapName;


    /**
     * Template method
     */
    @Override
    protected void setNames() {
        mapName           = "MAP_MODULES";
        requestTopicName  = "TOPIC_MODULES_STATE_CHANGED_REQUEST";
        responseTopicName = "TOPIC_MODULES_STATE_CHANGED_RESPONSE";

        // custom
        memberModuleMapName = "MAP_UUID_MODULES";
    }

    // ---------------------------------------------------------

    @Override
    public MapConfig configureMap() {
        MapConfig result = super.configureMap();

        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setImplementation(new MapModulesStore());
        mapStoreConfig.setWriteDelaySeconds(0); // setup write-through with 0 sec delay
        result.setMapStoreConfig(mapStoreConfig);

        return result;
    }

    /**
     * Add memberUUID => moduleName Imap
     * @param config hazelcast Config
     */
    @Override
    public void addAdditionalConfiguration(Config config) {
        MapConfig result = new MapConfig(memberModuleMapName);
        config.addMapConfig(result);
    }
}
