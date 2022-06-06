package ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.core.data.organisation.OrganisationType;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetails;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;

import java.util.Map;

import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.getDomainName;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.*;

// Concrete Beans defined in StreamParserConfigurations !
// NO @Component annotation here !

/**
 * Parsing Camera from Alert (from alertManager)
 */
@Slf4j
public class CameraParser implements SubjectParser {

    // special mark for logging subsystem
    Marker unknownMarker = MarkerFactory.getMarker(UNKNOWN_CAMERA_MARKER);

    private final String alertName;

    private final CameraService cameraService;

    protected CameraParser(String alertName, CameraService cameraService) {
        this.alertName = alertName;
        this.cameraService = cameraService;
    }

    @Override
    public String getId() {
        return alertName;
    }



    @Override
    public Subject parse(Alert alert, JSONObject event) {

        Subject result = null;

        JSONObject labels = event.getJSONObject("labels");
        String name = labels.getString("stream");
        String instance = alert.getInstance();
        String domainName = getDomainName(instance);
        String hostname = domainName.split("\\.", 2)[0].toLowerCase();

        Camera camera = cameraService.get(hostname, name);

        // хорошая камера, hostname стримера и имя камеры
        if(camera!= null) {
            result = camera;
            if(camera.getDetails() == null) {
                log.error("CAMERA NO DETAILS =======================================: {}", camera);
                Assert.notNull(camera.getDetails(), "camera.details == null");
            }
        }
        // Иначе приехала странная камера, о которой Watcher не знает
        // (либо такой камеры не существует в Watcher, либо она находится на другом стримере)
        if(result == null) {
            log.debug(unknownMarker, "UNKNOWN Alert camera: {}, using fake details", name);

            Map<String,String> referenceUrlMap = Map.of(PROMETHEUS_INSTANCE_KEY, alert.getInstance());

            // Set title as name
            String title = name;
            Camera unknownCamera = new Camera(name, title, domainName, hostname, referenceUrlMap,
                name + " UNKNOWN",null, null, null, OrganisationType.UNKNOWN,
                null, null);

            CameraDetails fakeDetails = new CameraDetails(name, Sets.newHashSet(CameraGroup.UNKNOWN));
            fakeDetails.setCommentExt("Fake Details");

            unknownCamera.setDetails(fakeDetails);

            result = unknownCamera;

        }
        return result;
    }
}


//result = new DefaultSubject(name, alert.getTitle() + " !UNKNOWN!",  Map.of(PROMETHEUS_KEY, alert.getInstance()));