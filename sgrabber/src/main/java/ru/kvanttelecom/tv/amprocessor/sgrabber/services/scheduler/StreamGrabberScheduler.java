package ru.kvanttelecom.tv.amprocessor.sgrabber.services.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.sgrabber.services.CameraCacheSynchronizer;


/**
 * Schedule StreamCacheFiller execution
 */
@Service
@Slf4j
public class StreamGrabberScheduler {

    @Autowired
    private CameraCacheSynchronizer cameraCacheSynchronizer;

    //@Scheduled(fixedDelayString = "#{new Long(prProperties.prometheus.evaluateDuration.toSeconds() * 1000).toString()}", initialDelay = 1 * 1000)
    @Scheduled(fixedDelay = 1 * 60000, initialDelay = 1 * 1000)
    public void syncSubjects() {

        //log.debug("Merging cameras to cache ...");
        cameraCacheSynchronizer.synchronize();
        //log.debug("Merging cameras to cache: done");
    }



}


//        try {
//
//            // execute only 1 time on startup - init
//            if(!cacheSettings.isReady()) {
//                try {
//                    // Load all details from DB -> Detail Cache
//                    detailsInitializer.initialize();
//                    // Mark internal cameras as INTERNAL
//                    internalInitializer.initialize();
//                }
//                catch (Exception rethrow) {
//                    log.error("ERROR on sgrabber initialization: ", rethrow);
//                    throw new RuntimeException(rethrow);
//                }
//            }
//
//
//
//            log.debug("Merging cameras to cache ...");
//            cameraCacheSynchronizer.synchronize();
//            log.debug("Merging cameras to cache: done");
//
//        }
//        catch (Exception rethrow) {
//            log.error("Scheduler syncSubjects error: ", rethrow);
//            throw new RuntimeException(rethrow);
//        }
//
//        if(!cacheSettings.isReady()) {
//            log.info("Caches have been initialized ..........");
//        }
//
//        cacheSettings.setReady();


/*
//    @PostConstruct
//    private void init() {
//        log.trace("INIT!!!");
//    }
 */
