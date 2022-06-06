package ru.kvanttelecom.tv.amprocessor.flexporter.services;

import com.github.anhdat.PrometheusApiClient;
import com.github.anhdat.models.VectorResponse;
import com.github.anhdat.models.VectorResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetails;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;
import ru.kvanttelecom.tv.amprocessor.flexporter.data.schema.prometheus.prometheus_series;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.UNKNOWN_MARKER;

/**
 * Check that camera has stable signal > [1h] or camera was created > [2w]
 *   then remove camera group [DEPLOYING]
 */
@Component
@Slf4j
public class CameraDeployingGroupRemover {

    private static final int DAYS_COUNT_CAMERA_IS_EXISTS = 14;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private PrometheusApiClient prometheusClient;


    public void remove() {
        removeOneHourWorking();
        removeExistsMoreThanTwoWeeks();
    }




    // If camera worked more than 1 hour without glitches
    // and consist in a group [DEPLOYING] - then remove camera from this group
    private void removeOneHourWorking() {

        VectorResponse response = null;
        try {
            // Get all cameras that alive[1h] = 0.999
            response = prometheusClient.query(prometheus_series.flussonic.expressions.stream_alive_1h_999);
        } catch (IOException e) {
            log.error("Prometheus api call failed:", e);
            return;
        }


        if(response == null || response.data == null) {
            throw new IllegalArgumentException("Prometheus - no response");
        }

        List<Camera> toRemoveDeploying = new ArrayList<>();

        for (VectorResult vector : response.data.result) {
            String instance = vector.metric.get(prometheus_series.flussonic.tags.instance);
            String hostname = instance.split("\\.", 2)[0].toLowerCase();
            String name = vector.metric.get(prometheus_series.flussonic.tags.stream);
            //float alive1h = vector.value.get(1);

            Camera camera = cameraService.get(name);
            if(camera != null) {
                toRemoveDeploying.add(camera);
            }
            else {
                log.debug(UNKNOWN_MARKER, "Camera {}.{} not found in CameraService, skip", hostname, name);
            }
        }
        removeGroupDeploying(toRemoveDeploying);
    }


    // If camera exists more than 2 weeks
    // and consist in a group [DEPLOYING] - then remove camera from this group
    private void removeExistsMoreThanTwoWeeks() {

        List<Camera> toRemoveDeploying = new ArrayList<>();

        cameraService.getMap().forEach((name, camera) -> {

            CameraDetails details = camera.getDetails();
            Duration d = Duration.between(details.getCreated(), Instant.now());
            if(d.toDays() > DAYS_COUNT_CAMERA_IS_EXISTS) {
                toRemoveDeploying.add(camera);
            }
        });
        removeGroupDeploying(toRemoveDeploying);
    }


    private void removeGroupDeploying(List<Camera> list) {
        int removedCount = 0;

        for (Camera camera : list) {
            Set<CameraGroup> groups = camera.getDetails().getGroups();
            if(groups.contains(CameraGroup.DEPLOYING)) {
                log.debug("Removing group [DEPLOYING] work [1h]: {}", camera.getName());
                groups.remove(CameraGroup.DEPLOYING);
                removedCount++;
                // update camera in cache
                cameraService.save(camera);
            }
        }
        if(removedCount > 0) {
            log.debug("Removed [DEPLOYING]: {}", removedCount);
        }
    }
}
