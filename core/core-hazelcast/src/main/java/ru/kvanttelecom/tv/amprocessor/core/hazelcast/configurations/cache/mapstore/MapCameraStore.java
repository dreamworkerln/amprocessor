package ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.cache.mapstore;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.MapStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Map;

@Slf4j
public class MapCameraStore implements MapStore<String, Camera> {

//    @Autowired
//    private HazelcastInstance instance;


//    @PostConstruct
//    private void init() {
//
//        MapConfig mapConfig = instance.getConfig().getMapConfig(MAP_CAMERAS);
//
//        MapStoreConfig mapStoreConfig = new MapStoreConfig();
//        mapStoreConfig.setImplementation(this);
//        // write-through == 0, sync | write-behind > 0, async
//        mapStoreConfig.setWriteDelaySeconds(0);
//        //
//        mapConfig.setMapStoreConfig(mapStoreConfig);
//    }


    @Override
    public void store(String key, Camera value) {
        log.debug("store: {}:{}", key, value);
    }

    @Override
    public void storeAll(Map<String, Camera> map) {
        log.debug("storeAll: {}", map);
    }

    @Override
    public void delete(String key) {
        log.debug("delete: {}", key);
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        log.debug("deleteAll: {}", keys);
    }

    @Override
    public Camera load(String key) {
        log.debug("load: {}", key);
        return null;
    }

    @Override
    public Map<String, Camera> loadAll(Collection<String> keys) {
        log.debug("loadAll: {}", keys);
        return null;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        log.debug("loadAllKeys");
        return null;
    }
}
