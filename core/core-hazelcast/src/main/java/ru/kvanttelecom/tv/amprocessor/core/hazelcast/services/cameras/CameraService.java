package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.BaseCacheService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.configurations.CamerasCacheConfiguration;

import java.util.*;

/**
 * Provide information about cameras from hazelcast map
 */
@Component
@Slf4j
public class CameraService extends BaseCacheService<String, Camera, Set<Camera>, Void> {

    @Autowired
    public void setCacheConfiguration(CamerasCacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }


     /**
     * Get camera by hostname and stream(camera) name
     * <br> Use this method to get cameras from Flussonic Media server.
     * @param hostname hostname
     * @param name camera name
     * @return Camera or null
     */
    public Camera get(String hostname, String name) {
        Camera tmp = super.get(name);
        return tmp != null && tmp.getHostname().equals(hostname) ? tmp : null;
    }


//    /**
//     * Get camera by camera name
//     * <br>
//     * Should use only for cameras from trusted sources (not from flussonic streamers!)
//     * <br>Flussonic Streamer may contain camera with same name, but unknown to Watcher (for example - old moved camera)
//     * @param name camera name
//     * @return Camera or null
//     */
//    @Override
//    public Camera get(String name) {
//        return super.get(name);
//    }


    public Camera save(Camera camera) {
        put(camera.getName(), camera);
        return camera;
    }


    public void flushCameras(Set<Camera> set) {
        sendRequest(nextId(),set);
    }

    public void flushCamerasSync(Set<Camera> set) {
        sendRequestSync(set);
    }

}




//        return StreamSupport.stream(list.spliterator(), false)
//            .map(camera -> cameraCache.set(camera))
//            .collect(Collectors.toList());





//    private IMap<String,Camera> map;
//    private ITopic<Set<String>> topic;
//    private UUID flushRegistration;
//
//
//    @Autowired
//    private HazelcastInstance instance;
//
//    private final Delegate<Set<String>> flushDelegate = new Delegate<>();
//
//    @PostConstruct
//    private void init() {
//        map = instance.getMap(MAP_CAMERAS);
//        topic = instance.getTopic(TOPIC_CAMERAS_FLUSH);
//    }
//
//    public Camera get(String name) {
//        return map.get(name);
//    }
//
//    /**
//     * Save camera to cache
//     */
//    public Camera save(Camera camera) {
//        map.put(camera.getName(), camera);
//        return camera;
//    }
//
////    public List<Camera> saveList(Iterable<Camera> list) {
////        list.forEach(this::save);
////        return Lists.newArrayList(list);
////    }
//
//    public void delete(Camera camera) {
//        map.remove(camera.getName());
//    }
//
//    public void delete(String name) {
//        map.remove(name);
//    }
//
////    public void deleteList(Iterable<Camera> list) {
////        list.forEach(camera -> map.remove(camera.getName()));
////    }
//
//
//
//    public List<Camera> getList() {
//        return new ArrayList<>(map.values());
//    }
//
//    public Map<String, Camera> getMap() {
//        return map;
//    }
//
//    public long count() {
//        return map.size();
//    }
//
//    public boolean containsKey(String name) {
//        return map.containsKey(name);
//    }






// -------------------------------------------------------------------

//    /**
//     * Flush cameras.details to DB
//     */
//    public void flushCameras(Set<String> set) {
//        topic.publish(set);
//    }
//
//
//    /**
//     * Flush camera.details to DB
//     */
//    public void flushCamera(Camera camera) {
//        topic.publish(Set.of(camera.getName()));
//    }






//    /**
//     * Add camera to flush list
//     */
//    public void flushAdd(String name) {
//        flush.add(name);
//    }
//
//    /**
//     * Flush all cameras.detail from flush list to DB
//     */
//    public void flushAll() {
//    }
//
//
//    /**
//     * Flush one camera to DB
//     */
//    public void flush(String name) {
//        flush.add(name);
//    }

// ------------------------------------------------------------------------

//    private Camera saveNoIntercept(Camera camera) {
//        camera.setIntercept(false);
//        map.put(camera.getName(), camera);
//        return camera;
//    }





//    public CameraServiceEx getCameraServiceEx() {
//        CameraService self = this;
//        return new CameraServiceEx() {
//            @Override
//            public CameraService cameraService() {
//                return self;
//            }
//            @Override
//            public Camera saveNoIntercept(Camera camera) {
//                return self.saveNoIntercept(camera);
//            }
//        };
//    }



//    public void addEntryListener(@Nonnull MapListener listener,
//                                 @Nonnull Predicate<String, Camera> predicate,
//                                 boolean includeValue) {
//        map.addEntryListener(listener, predicate, includeValue);
//
//
//    }

//    public void addEntryListenerConfig(Class<?> listenerClass) {
//        instance.getConfig().getMapConfig(CAMERAS).addEntryListenerConfig(
//            new EntryListenerConfig(listenerClass.getName(), true, true));
//    }

//    public void addPutHandler(Consumer<EntryEvent<String, Camera>> handler) {
//        map.addEntryListener(new DetailsEntryListener(handler), p -> true, true);
//    }
