package ru.kvanttelecom.tv.amprocessor.flexporter.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Import streams metrics from prometheus,
 * alter them(add stream description label from cache)
 * and export it back to prometheus
 */
@Service
@Slf4j
public class FlussonicExporterScheduler {

    @Autowired
    FlussonicExporter exporter;

    @Autowired
    CameraDeployingGroupRemover cameraDeployingGroupRemover;

    @PostConstruct
    private void init() {
        log.trace("INIT!!!");
    }


    /**
     * Export streams metrics to prometheus
     */
    @Scheduled(fixedDelay = 1 * 5000, initialDelay = 0 * 1000)
    public void syncAll() {
        exporter.exportStreams();
    }


    @Scheduled(fixedDelay = 1 * 60000, initialDelay = 0 * 1000)
    public void removeDeployingCameras() {
        cameraDeployingGroupRemover.remove();
    }



}



//@Scheduled(fixedDelayString = "#{new Long(prProperties.prometheus.evaluateDuration.toSeconds() * 1000).toString()}", initialDelay = 1 * 1000)

