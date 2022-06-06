package ru.kvanttelecom.tv.amprocessor.mailer.data.mail.filtres._base;

import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Check Alert to satisfy specified conditions
 */
public abstract class AbstractAlertFilter implements AlertFilter {

    protected List<Predicate<Alert>> filters = new ArrayList<>();

    public boolean test(Alert alert) {
        Predicate<Alert> result = filters.stream().reduce(Predicate::and).orElse(x -> false);
        return result.test(alert);
    }

    /**
     * Return filter predicate, consisting from all combined filters from subclass to superclass
     */
    public Predicate<Alert> predicate() {
        return filters.stream().reduce(Predicate::and).orElse(x -> false);
    }

    public static Camera fromAlert(Alert alert) {
        return (Camera) alert.getSubject();
    }
}
