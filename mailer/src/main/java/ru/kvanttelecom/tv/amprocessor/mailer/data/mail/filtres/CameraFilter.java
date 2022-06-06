package ru.kvanttelecom.tv.amprocessor.mailer.data.mail.filtres;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;
import ru.kvanttelecom.tv.amprocessor.mailer.data.mail.filtres._base.AbstractAlertFilter;

import java.util.function.Predicate;

import static ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertName.STREAM_DOWN;

/**
 * Alert.subject is Camera, Camera is enabled
 */
@Component
@Slf4j
public class CameraFilter extends AbstractAlertFilter {

    public CameraFilter() {

        // is camera
        Predicate<Alert> cameraF = c -> c.getSubject() instanceof Camera;

        // camera is enabled
        Predicate<Alert> cameraEnbF = cameraF.and(a -> {

            Camera c = fromAlert(a);
            Assert.notNull(c, "camera == null");
            Assert.notNull(c.getDetails(), "camera.details == null");

            return c.isEnabled();
        });
        filters.add(cameraEnbF);
    }
}
