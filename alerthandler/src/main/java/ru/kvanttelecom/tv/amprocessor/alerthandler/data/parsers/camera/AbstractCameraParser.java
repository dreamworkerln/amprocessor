package ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers.camera;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers.SubjectParser;
import ru.kvanttelecom.tv.amprocessor.core.data.organisation.OrganisationType;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraDetails;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;

import java.util.Map;

import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.formatMsg;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.*;

// Concrete Beans defined in StreamParserConfigurations !
// NO @Component annotation here !

/**
 * Parsing Camera from Alert (from alertManager)
 */
@Slf4j
public abstract class AbstractCameraParser implements SubjectParser {

    // special mark for logging subsystem
    Marker unknownMarker = MarkerFactory.getMarker(UNKNOWN_CAMERA_MARKER);

    private final String alertName;

    protected final CameraService cameraService;

    public AbstractCameraParser(String alertName, CameraService cameraService) {
        this.alertName = alertName;
        this.cameraService = cameraService;
    }

    @Override
    public String getId() {
        return alertName;
    }


    @Override
    public Subject parse(Alert alert, JSONObject event) {

        Subject result;
        Camera camera = getCamera(alert, event);

        // ASSERT -----------------------------------------------------------------------------
        // Этого не должно произойти - если произошло - сломана логика работы программы
        Assert.notNull(camera.getDetails(),
            "camera.details == null\n" +
                formatMsg("CAMERA NO DETAILS =======================================: {}", camera));
        // -----------------------------------------------------------------------------------

        return camera;
    }

    protected abstract Camera getCamera(Alert alert, JSONObject event);


    protected Camera createUnknownCamera(String name, String domainName, String hostname, String instance) {

        Camera result;

        log.debug(unknownMarker, "UNKNOWN Alert camera: {}, using fake details", name);

        Map<String,String> referenceUrlMap = Map.of(PROMETHEUS_INSTANCE_KEY, instance);

        // Set title as name
        String title = name;
        result = new Camera(name, title, domainName, hostname, referenceUrlMap,
            name + " UNKNOWN",null, null, null, OrganisationType.UNKNOWN,
            null, null);

        CameraDetails fakeDetails = new CameraDetails(name, Sets.newHashSet(CameraGroup.UNKNOWN));
        fakeDetails.setCommentExt("Fake Details");

        result.setDetails(fakeDetails);

        return result;
    }


}


//result = new DefaultSubject(name, alert.getTitle() + " !UNKNOWN!",  Map.of(PROMETHEUS_KEY, alert.getInstance()));