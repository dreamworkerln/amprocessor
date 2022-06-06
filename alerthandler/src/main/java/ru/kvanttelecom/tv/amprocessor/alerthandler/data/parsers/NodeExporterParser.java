package ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers;

import org.json.JSONObject;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.DefaultSubject;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;

import java.util.Map;

import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.PROMETHEUS_INSTANCE_KEY;

//@Component
public class NodeExporterParser implements SubjectParser {

    @Override
    public String getId() {
        return "InstanceDown";
    }


    @Override
    public Subject parse(Alert alert, JSONObject event) {
        String name = alert.getInstance();
        // Surrogate Subject
        return new DefaultSubject(name, alert.getTitle(), Map.of(PROMETHEUS_INSTANCE_KEY, alert.getInstance()));
    }
}
