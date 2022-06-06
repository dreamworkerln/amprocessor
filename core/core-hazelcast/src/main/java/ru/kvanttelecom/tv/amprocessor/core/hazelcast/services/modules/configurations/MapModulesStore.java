package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.configurations;


import com.hazelcast.map.MapStore;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.data.ModuleState;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *  Map modules storage, if module not found => return ModuleState.DOWN
 */
public class MapModulesStore implements MapStore<String, ModuleState> {

    private final ConcurrentMap<String, ModuleState> storage = new ConcurrentHashMap<>();

    @Override
    public void store(String key, ModuleState value) {
        storage.put(key, value);
    }

    @Override
    public void storeAll(Map<String, ModuleState> map) {
        storage.putAll(map);
    }

    @Override
    public void delete(String key) {
        storage.remove(key);
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        storage.keySet().removeAll(keys);
    }

    @Override
    public ModuleState load(String key) {
        ModuleState result = storage.get(key);
        return result != null ? result : ModuleState.DOWN;
    }

    @Override
    public Map<String, ModuleState> loadAll(Collection<String> keys) {
        return storage;
    }

    @Override
    public Iterable<String> loadAllKeys() {
        return storage.keySet();
    }
}
