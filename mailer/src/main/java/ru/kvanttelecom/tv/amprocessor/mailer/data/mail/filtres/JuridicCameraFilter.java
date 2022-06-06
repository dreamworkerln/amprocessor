package ru.kvanttelecom.tv.amprocessor.mailer.data.mail.filtres;

import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Alert.subject is Сamera, Camera is enabled
 * Camera not UNKNOWN and
 * Camera not INTERNAL and
 * Camera is JURIDIC
 */
@Component
public class JuridicCameraFilter extends PublicCameraFilter {

    public JuridicCameraFilter() {
        Predicate<Alert>  juridicF = a -> {
            Camera c = fromAlert(a);
            Set<CameraGroup> groups = c.getDetails().getGroups();
            return groups.contains(CameraGroup.JURIDIC);
        };
        filters.add(juridicF);
    }
}
