package ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers;

import org.json.JSONObject;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;

/**
 * Retrieve subject from alert (from other services)
 * Should be implemented for every Prometheus Alert type
 */
public interface SubjectParser {
    String getId();
    Subject parse(Alert alert, JSONObject event);
}
