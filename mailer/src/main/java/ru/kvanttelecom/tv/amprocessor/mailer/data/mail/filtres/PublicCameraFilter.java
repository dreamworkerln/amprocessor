package ru.kvanttelecom.tv.amprocessor.mailer.data.mail.filtres;

import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;
import ru.kvanttelecom.tv.amprocessor.mailer.data.mail.filtres._base.AbstractAlertFilter;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Alert.subject is Camera, Camera is enabled
 * Camera not UNKNOWN and
 * Camera not INTERNAL
 */
@Component
public class PublicCameraFilter extends CameraFilter {

    public PublicCameraFilter() {
        Predicate<Alert> publicF = a -> {
            Camera c = fromAlert(a);
            Set<CameraGroup> groups = c.getDetails().getGroups();

            return
                !groups.contains(CameraGroup.UNKNOWN) &&
                !groups.contains(CameraGroup.INTERNAL) &&
                !groups.contains(CameraGroup.DEPLOYING);
        };
        filters.add(publicF);
    }
}
