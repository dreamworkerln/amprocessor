package ru.kvanttelecom.tv.amprocessor.sgrabber.services;

import com.google.common.collect.Maps;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionalMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.data.ModuleState;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.ModulesService;
import ru.kvanttelecom.tv.amprocessor.sgrabber.services.flussonic.downloaders.WatcherOrganizationDownloader;
import ru.kvanttelecom.tv.amprocessor.sgrabber.services.flussonic.downloaders.WatcherStreamDownloader;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Download streams info from watcher and save it to cache
 */
@Service
@Slf4j
public class CameraCacheSynchronizer {

    @Autowired
    private WatcherStreamDownloader streamDownloader;

    @Autowired
    private WatcherOrganizationDownloader orgDownloader;

    @Autowired
    CameraService cameraService;

    @Autowired
    private ModulesService modulesService;

    /**
     * Download Camera information from flussonic watcher
     * And CameraDetails from DB
     * <br>
     * Merge both to cache
     */
    public void synchronize() {

        // Download all organizations
        Map<Long,String> organizations = orgDownloader.getAll();

        // Download all streams info from watcher
        List<Camera> list = streamDownloader.getAll(organizations);

        // Test input correctness
        containsBothRussianAndEnglishChars(list);

        // Cameras to update from
        Map<String, Camera> update = Maps.uniqueIndex(list, Camera::getName);



//        // INITIALIZATION ---------------------------------------------------------------------
//        if(!cacheSettings.isReady()) {
//            log.debug("INITIALIZING CameraCache");
//
//
//            // MARK INTERNAL CAMERAS (if not already marked) inside DB (create/update)
//            internalInitializer.mark();
//
//
//            // get all cameraDetails from DB
//            ConcurrentMap<String,CameraDetails> detailsMap =
//                converter.toDtoList(detailsService.findAll()).stream()
//                    .collect(Collectors.toConcurrentMap(CameraDetails::getName, Function.identity()));
//
//            log.debug("Loaded from DB: {} details", detailsMap.size());
//
//            // Synchronize detailsMap(and DB) with update
//            if(update.size() > 0) { // watcher maybe return empty cameras list on malfunction
//
//                // Delete deprecated
//                detailsMap.forEach((name, detail) -> {
//
//                    if(!update.containsKey(name)) {
//                        log.debug("Delete from CameraCache and CameraDetails: {}", name);
//                        detailsService.delete(name);
//                        detailsMap.remove(name);
//                    }
//                });
//
//                // Create/update
//                update.forEach((name, camera) -> {
//                    // create
//                    if(!detailsMap.containsKey(name)) {
//                        log.debug("Add to CameraCache and CameraDetails: {}", name);
//                        detailsService.save(CameraDetailsEntity.createEmpty(name));
//                        detailsMap.put(name, new CameraDetails(name, Sets.newHashSet()));
//                    }
//                    // no update
//                });
//            }
//
//            // Assign detail to update.camera (if this details exists)
//            update.forEach((name, camera) -> camera.setDetails(detailsMap.get(name)));
//
//            log.debug("INITIALIZING done .......");
//        }
//        // END INITIALIZATION ----------------------------------------------------------



        // INITIALIZATION ----------------------------------------------------------------
//        if(!cacheSettings.isReady()) {
//            log.debug("INITIALIZING CameraCache");
//
//            // MARK INTERNAL CAMERAS (if not already marked) inside DB (create/update)
//            internalInitializer.mark();
//            log.debug("INITIALIZING CameraCache DONE");
//        }
        // END INITIALIZATION ------------------------------------------------------------

        // Merging update to cameraCache

        Map<String, Camera> cameraCache = cameraService.getMap();

        // Preserve camera.details
        // Watcher doesn't know about details - all update.details == null
        // Assign details to update cameras (from camera cache)
        update.forEach((name, camera) -> {
            if(cameraCache.containsKey(name)) {
                camera.setDetails(cameraCache.get(name).getDetails());
            }
        });



        // camera.details required to persist
        Set<Camera> flush = new HashSet<>();

        // Mirror updates to camera cache
        // --------------------------------------------------------------

        // delete deprecated (from camera cache only, do not flush to DB)
        cameraCache.forEach((name, camera) -> {
            if (!update.containsKey(name)) {

                log.debug("Removing camera from cache: {}", name);
                cameraService.delete(name);
                // do not delete camera details, let it stay (no DB gc vacuum cleaning)
            }
        });


        // Create/update
        update.forEach((name, camera) -> {

            // create
            if (!cameraCache.containsKey(name)) {
                // create Camera
                log.debug("Creating Camera: {}", name);
                flush.add(camera);
            }
            // update
            else {
                Camera oldCamera = cameraCache.get(name);

                if (!camera.equals(oldCamera)) {
                    // update camera
                    log.debug("Updating Camera: {}", name);
                    flush.add(camera);
                }
            }
        });

        if (flush.size() > 0) {
            //log.debug("Flushing cameras: {}", flush);
            log.debug("Flushing cameras: {}", flush.size());

            cameraService.flushCamerasSync(flush);

            log.debug("Flushing cameras done.");
        }

        // Initialize INTERNAL cameras on startup
        if (modulesService.getState() == ModuleState.PENDING) {
            if (flush.size() > 0) {
                markInternal();
            }
        }



        // Finish hazelcast map transaction


        // MODULE IS READY: SGRABBER ==============
        modulesService.ready();
        // ========================================


        // Asserting check
        cameraService.getMap().forEach((name, camera) -> {
            if(camera.getDetails() == null) {
                log.error("Camera DETAILS == null: {}", camera);
                Assert.notNull(camera.getDetails(), "camera.details == null");
            }
        });
    }


    /**
     * Check Camera.title, Camera.organisation for containing both russian and english chars
     * @param list
     */
    private void containsBothRussianAndEnglishChars(List<Camera> list) {

        //final String REGEX = "\\p{IsLatin}";
        // create a pattern
        final String regex = "\\b(?=\\p{L}*[a-zA-Z])(?=\\p{L}*[\\u0400-\\u04FF])[a-zA-Z\\u0400-\\u04FF]+\\b";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.UNICODE_CASE);

        for (Camera camera : list) {

            String title = camera.getTitle();
            Matcher matcher = pattern.matcher(title);

            if (matcher.find()) {
                log.debug("Camera {} contains mixed title: {}", camera.getName(), camera.getTitle());
            }

            String organisation = camera.getOrganization();
            matcher = pattern.matcher(organisation);
            if (matcher.find()) {
                log.debug("Camera {} {} contains mixed organisation: {}", camera.getName(), camera.getTitle(), organisation);
            }
        }
    }



    private List<String> getInternalNames() {
        List<String> result = new ArrayList<>();

        // Кабинет ОПР
        result.addAll(List.of("ag-9311604", "ntcnbrt-d48af55f4c", "opr.krutilka.test-4efdda7ac2"));
        // Склад
        result.addAll(List.of("ag-18311690"));

        return result;
    }


    /**
     * Initialize INTERNAL cameras
     */
    public void markInternal() {

        List<String> list = getInternalNames();

        log.info("Mark cameras as INTERNAL at startup: {}", list);

        Set<Camera> flush = new HashSet<>();
        for (String name : list) {
            Camera camera = cameraService.get(name);
            if(camera != null) {

                Set<CameraGroup> groups = camera.getDetails().getGroups();
                if (!groups.contains(CameraGroup.INTERNAL)) {
                    groups.add(CameraGroup.INTERNAL);
                    flush.add(camera);
                }
            }
        }
        // Write to DB
        cameraService.flushCamerasSync(flush);

        log.info("Mark cameras as INTERNAL at startup done.");
    }

}






//log.debug("t8.testcam-92a92ba70b: {}", cache.get(StreamKey.fromString("t8.testcam-92a92ba70b")));

        /*
        // exporting all streams
        try {
            String s = mapper.writeValueAsString(cache.getMap());
            Files.writeString(Paths.get("cameras.json"), s , StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        } catch (Exception e) {
            log.warn("Error: ", e);
        }
        */
