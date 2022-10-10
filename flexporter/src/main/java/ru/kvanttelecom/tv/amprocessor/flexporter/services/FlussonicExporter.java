package ru.kvanttelecom.tv.amprocessor.flexporter.services;

import com.github.anhdat.PrometheusApiClient;
import com.github.anhdat.models.VectorResponse;
import com.github.anhdat.models.VectorResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.ModulesService;
import ru.kvanttelecom.tv.amprocessor.core.prometheus.data.schema.prometheus_series;

import java.util.HashMap;
import java.util.Map;

import static ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera.INCOMPLETE_CAMERA_IMPORT;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.*;


/**
 * Merge Camera from cache to CameraState
 *
 */

@Service
@Slf4j
public class FlussonicExporter {

    // Rest api prometheus client (read time-series data from prometheus)
    @Autowired
    private PrometheusApiClient prometheusClient;

    @Autowired
    private CameraService cameraService;


    // StreamStates from prometheus
    @Autowired
    private CameraStateService stateService;

    // Prometheus java exporter (to write data to prometheus)
    @Autowired
    private MeterService meterService;

    @Autowired
    private ModulesService modulesService;

//    @Autowired
//    private FlussonicExporterProperties properties;

    /**
     * Import camera metrics from prometheus.
     * Modify metrics (add additional labels, add camera ).
     * Export modified metrics back to prometheus.
     */
    public void exportStreams() {

        // Load streams aliveness state from prometheus (up/down)
        // with Map<StreamKey,Instance> (need for creation new )
        //Pair<Map<StreamKey, Boolean>, Map<StreamKey,String>> tmp = loadUpdateFromPrometheus();
        //Map<StreamKey, Boolean> update = tmp.getLeft();

        Map<String, HostCameraAlive> update = loadUpdateFromPrometheus();


        //Map<StreamKey, String> instances = tmp.getRight();
        // Updating StreamStates -----------------------------------

        // THREE STEP MIRROR MERGE ----------------------------

        // REMOVE IF NOT EXISTS - remove deprecated metrics (exists locally in stateService, not present in update)
        for (String name : stateService.keySet()) {
            if(!update.containsKey(name)) {

                log.debug("Remove camera {}", name);
                stateService.remove(name);
                meterService.removeMetrics(name);
            }
        }

        for (String name : update.keySet()) {

            // Get camera from watcher
            Camera camera = cameraService.get(name);

            HostCameraAlive hna = update.get(name);

            Assert.notNull(camera, "Watcher camera == null");
//            // skip this unknown camera
//            if(camera == null) {
//                //camera = createSurrogateStream(key, instances.get(key));
//                log.debug(unknownMarker, "Camera {}.{} not found in cameraService, skip", hna.host, hna.name);
//                // store it to StreamCache
//                //streamCache.put(name, camera);
//                continue;
//            }

            boolean isAlive = update.get(name).alive;

            // CREATE NEW - В stateService нет такой camera
            if (!stateService.containsKey(name)) {


                String dbgMsg = camera.getComment().equals(INCOMPLETE_CAMERA_IMPORT) ?
                    "Create SURROGATE CameraState: {}" :   // ????????????????
                    "Create CameraState: {}";
                log.debug(dbgMsg, name);

                stateService.create(camera);
                meterService.addMetrics(camera);
            }
            // UPDATE EXISTING - Camera изменилась (если в stateService устаревшая версия) -
            else {
                Camera cameraToUpdate = stateService.get(name).getCamera();

                // Если изменились поля стрима - title, комментарий и т.д.
                if (!camera.equals(cameraToUpdate)) {

                    log.debug("Update camera {}", name);
                    // remove old metrics
                    meterService.removeMetrics(name); // в метрике используется title, придется удалить
                    // add new camera
                    stateService.updateCamera(name, camera);
                    // add new metrics
                    meterService.addMetrics(camera);
                }
            }
            

            // update camera state
            stateService.update(name, isAlive);
        }

        // MODULE IS READY: FLEXPORTER ==============
        modulesService.ready();
        // ========================================
    }

    //private Pair<Map<StreamKey, Boolean>, Map<StreamKey,String>> loadUpdateFromPrometheus() {
    @SneakyThrows
    private Map<String, HostCameraAlive>  loadUpdateFromPrometheus() {

        Map<String, HostCameraAlive> result = new HashMap<>(); // map живости камер
        //Map<StreamKey, String> instances = new HashMap<>(); // map instances камер

        // Get camera aliveness from prometheus for last 15s
        VectorResponse response = prometheusClient.query(prometheus_series.flussonic.expressions.rate_flussonic_stream_bytes_in_15s);

        if(response == null || response.data == null) {
            throw new IllegalArgumentException("Prometheus - no response");
        }

        for (VectorResult vector : response.data.result) {
            String instance = vector.metric.get(prometheus_series.flussonic.tags.instance);
            String hostname = instance.split("\\.", 2)[0].toLowerCase();
            String name = vector.metric.get(prometheus_series.flussonic.tags.stream);
            boolean alive = vector.value.get(1) > 0; // strictly greater than 0.0

            // validating camera from Prometheus

            // хорошая камера, присутствуют hostname стримера и имя камеры
            Camera camera = cameraService.get(hostname, name);
            if(camera != null) {
                result.put(name, new HostCameraAlive(hostname, name, alive));
            }
            else {
                // плохая неизвестная камера, попала в prometheus с какого-то стримера,
                // Watcher об этой камере не знает
                log.debug(UNKNOWN_MARKER, "Camera {}.{} not found in cameraService, skip", hostname, name);
            }
        }
        return result;
    }

    @Data
    @AllArgsConstructor
    private static class HostCameraAlive {
        private String host;   // hostname
        private String name;   // camera name
        private boolean alive; // is camera alive
    }

}





//            if(result.containsKey(name)) {
//                log.info(duplicateMarker, "Warning, DUPLICATE camera name: {}; old: {}, new: {}",
//                    name,
//                    result.get(name).host + "." + result.get(name).name,
//                    hostname + "." + name);
//            }


//instances.put(key,domainName);




//    @SneakyThrows
//    /**
//     * Create camera that not exists on Watcher, exists on MediaServer only
//     */
//    public Stream createSurrogateStream(StreamKey key, String domainName) {
//
//        String name = key.getName();
//        String title = name;
//        String hostname = key.getHostname();
//
//        String mediaserverStreamUrl = "http://" + domainName + MEDIASERVER_STREAM_PATH + name;
//
//        String grafanaStreamUrl =
//            properties.getGrafanaUrl() +
//                GRAFANA_STREAM_VIEW_QUERY +
//                URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
//
//        Map<String,String> referenceUrlMap = Map.of(GRAFANA_KEY, grafanaStreamUrl, MEDIASERVER_KEY, mediaserverStreamUrl);
//
//        return new Camera(key, null, hostname, referenceUrlMap, name, title,
//            INCOMPLETE_STREAM_IMPORT, null, null, null, null,
//            null, null);
//    }
//

// ===================================================================================







/*


//    private String getTitleFromCache(StreamKey key) {
//        String title = key.getName();
//        Subject sbj = streamCache.get(key.toString());
//
//        if(sbj != null) {
//            title = sbj.getTitle();
//        }
//        return title;
//    }







        /*
        // 1. get all stream from prometheus for last 5m -------------------------------
        VectorResponse response = prometheusClient.query(expressions.rate_flussonic_stream_bytes_in_5m);

        for (VectorResult vector : response.data.result) {
            StreamKey key = FlussonicExporterUtils.extractStreamKey(vector);
            result.put(key, false);
        }

        // Зачем нужный шаги 1. / 1.2 - 1.3 - не знаю, сразу спросить ?
        // Зачем спрашивать за последние 5m а потом за 5s ?


        // 1.2 get down streams -------------------------------------------------------
        response = prometheusClient.query(expressions.down_streams_15s);

        for (VectorResult vector : response.data.result) {
            StreamKey key = FlussonicExporterUtils.extractStreamKey(vector);
            if(result.containsKey(key)) {
                result.put(key, false);
            }
        }

        // 1.3 get up streams ----------------------------------------------------------
        response = prometheusClient.query(expressions.up_streams_15s);

        for (VectorResult vector : response.data.result) {
            StreamKey key = FlussonicExporterUtils.extractStreamKey(vector);
            if(result.containsKey(key)) {
                result.put(key, true);
            }
        }

*/