package ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers.camera;

import org.json.JSONObject;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;
import ru.kvanttelecom.tv.amprocessor.core.prometheus.data.schema.prometheus_series;


import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.getDomainName;
import static ru.kvanttelecom.tv.amprocessor.core.prometheus.data.schema.prometheus_series.flussonic;


/**
 * Parse cameras from alertmanager alerts
 * from metrics synthesized by flexporter
 */
public class SyntheticCameraParser extends AbstractCameraParser {


    public SyntheticCameraParser(String alertName, CameraService cameraService) {
        super(alertName, cameraService);
    }


    @Override
    protected Camera getCamera(Alert alert, JSONObject event) {

        Camera result;

        JSONObject labels = event.getJSONObject(prometheus_series.labels);
        String name = labels.getString(flussonic.tags.stream);
        String instance = alert.getInstance();
        String domainName = "UNKNOWN";
        String hostname = "UNKNOWN";

        //ASAP EDC - remove "else branch" after 2 weeks prometheus recording
        if(labels.has(flussonic.tags.host)) {
            hostname = labels.getString(flussonic.tags.host);
            result = cameraService.get(hostname, name);
        }
        else {
            result = cameraService.get(name);
        }
        
        if (result == null) {
            result = createUnknownCamera(name, domainName, hostname, instance);
        }
        return result;

    }

}
