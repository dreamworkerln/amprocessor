package ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers.camera;

import org.json.JSONObject;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;

import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.getDomainName;
import ru.kvanttelecom.tv.amprocessor.core.prometheus.data.schema.prometheus_series;
import static ru.kvanttelecom.tv.amprocessor.core.prometheus.data.schema.prometheus_series.flussonic;



/**
 * Parse cameras from alertmanager alerts
 * from Flussonic MediaServer
 */
public class MediaServerCameraParser extends AbstractCameraParser {

    public MediaServerCameraParser(String alertName, CameraService cameraService) {
        super(alertName, cameraService);
    }


    @Override
    protected Camera getCamera(Alert alert, JSONObject event) {
        Camera result;

        JSONObject labels = event.getJSONObject(prometheus_series.labels);
        String name = labels.getString(flussonic.tags.stream);

        String instance = alert.getInstance();
        String domainName = getDomainName(instance);
        String hostname = domainName.split("\\.", 2)[0].toLowerCase();

        result = cameraService.get(hostname, name);

        if (result == null) {
            result = createUnknownCamera(name, domainName, hostname, instance);
        }

        return result;

    }

}




//    /**
//     * Get camera from CameraService using hostname and stream
//     */
//    @Override
//    protected Camera getCamera(Alert alert, JSONObject event) {
//        JSONObject labels = event.getJSONObject("labels");
//        String name = labels.getString("stream");
//        String instance = alert.getInstance();
//        String domainName = getDomainName(instance);
//        String hostname = domainName.split("\\.", 2)[0].toLowerCase();
//        return cameraService.get(hostname, name);
//    }
