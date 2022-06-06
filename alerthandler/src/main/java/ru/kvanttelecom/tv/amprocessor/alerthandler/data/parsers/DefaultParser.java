package ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.DefaultSubject;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;

import java.util.Map;

import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.PROMETHEUS_INSTANCE_KEY;

/**
 * Generate subject from alert itself (default)
 */
@Component
public class DefaultParser implements SubjectParser {

    @Override
    public String getId() {
        return "";
    }

    @Override
    public Subject parse(Alert alert, JSONObject event) {
        // Surrogate Subject
        return new DefaultSubject(alert.getInstance(), alert.getTitle(), Map.of(PROMETHEUS_INSTANCE_KEY, alert.getInstance()));
    }
}
