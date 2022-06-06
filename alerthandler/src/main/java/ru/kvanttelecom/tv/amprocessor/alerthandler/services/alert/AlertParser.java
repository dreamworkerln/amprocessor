package ru.kvanttelecom.tv.amprocessor.alerthandler.services.alert;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers.SubjectParser;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertStatus;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;


import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.isBlank;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.INSTANT_ZERO;


/**
 * Parse prometheus alert (from self prometheus api or alertmanager)
 * One type, no subclasses, one bean
 */
@Component
@Slf4j
public class AlertParser {

    // Subject parser map
    private Map<String, SubjectParser> parsers;

    // -------------------------
    @Autowired
    private List<SubjectParser> autowireParser;

    // Build Subject parsers from @Autowired list to Map
    @PostConstruct
    private void init() {
        parsers = Maps.uniqueIndex(autowireParser, SubjectParser::getId);
    }
    // -------------------------

    /**
     * Parse Json Alert list -> List<Alert>
     * @param json
     * @return
     */
    public List<Alert> parse(String json) {

        List<Alert> result = new ArrayList<>();

        JSONObject alertListJson = new JSONObject(json);

        JSONArray list;
        // push
        if(alertListJson.has("alerts")) {
            list = alertListJson.getJSONArray("alerts");
        }
        // api response
        else if (alertListJson.has("data")) {
            JSONObject data = alertListJson.getJSONObject("data");
            list = data.getJSONArray("alerts");
        }
        else {
            throw new IllegalArgumentException("Bad json: \n" + json);
        }

        for (int i = 0; i < list.length(); i++) {
            JSONObject AlertJson = list.getJSONObject(i);
            Alert alert = parseAlert(AlertJson);
            result.add(alert);
        }
        return result;
    }


    // --------------------------------------------------------------------------------------

    /**
     * Parse single alertJson to Alert
     * @param alertJson
     * @return
     */
    protected Alert parseAlert(JSONObject alertJson) {

        Alert result = new Alert();

        // status -----------------------------------------
        if(alertJson.has("status")) {
            result.setStatus(AlertStatus.valueOf(alertJson.getString("status").toUpperCase()));
        }
        else if(alertJson.has("state")) {
            result.setStatus(AlertStatus.valueOf(alertJson.getString("state").toUpperCase()));
        }
        else {
            throw new IllegalArgumentException("Alert - status/state nod defined\n" + alertJson);
            //alert.setStatus(AlertStatus.FIRING);
        }



        // name -----------------------------------------
        JSONObject labels = alertJson.getJSONObject("labels");

        result.setName(labels.getString("alertname"));

        String inst = labels.getString("instance");
        result.setInstance(inst);
        result.setSeverity(labels.getString("severity"));
        JSONObject annotations = alertJson.getJSONObject("annotations");
        result.setTitle(annotations.getString("title"));
        result.setDescription(annotations.getString("description"));

        // set default duration
        result.setDuration(Duration.ZERO);

        // set startsAt and endsAt

        // via webhook push -----------------------------------------------------------
        if(alertJson.has("startsAt")) {
            result.setStartsAt(ZonedDateTime.parse(alertJson.getString("startsAt")).toInstant());
            result.setEndsAt(ZonedDateTime.parse(alertJson.getString("endsAt")).toInstant());

            // endsAt is defined
            if(!result.getEndsAt().equals(INSTANT_ZERO)) {
                result.setDuration(Duration.between(result.getStartsAt(), result.getEndsAt()));
            }
        }
        // via rest get response ---------------------------------------------------------
        else if(alertJson.has("activeAt")) {
            result.setStartsAt(ZonedDateTime.parse(alertJson.getString("activeAt")).toInstant());
            // endsAt not defined in prometheus response
            result.setEndsAt(INSTANT_ZERO);
            // этот запрос делает пользователь (или оно там кешируется TTL 1m),
            // поэтому укажем duration вручную:
            // Сколько на данным момент уже длится тревога, даже если она не завершилась к этому моменту
            result.setDuration(Duration.between(result.getStartsAt(), Instant.now()));
        }
        else {
            throw new IllegalArgumentException("Alert - startsAt/activeAt nod defined\n" + alertJson.toString());
        }

//        // Pending duration threshold (custom label)
//        if(labels.has("for")) {
//            String forr = labels.getString("for").trim().toLowerCase();
//            //result.setForr(forToDuration(forr));
//        }

        // parse / get subject
        if (isBlank(result.getName())) {
            throw new IllegalArgumentException("Alert - empty name\n" + alertJson.toString());
        }

        // Subject parsing

        // known alert type, or default parser (for unknown alert type)
        SubjectParser parser = parsers.getOrDefault(result.getName(), parsers.get(""));
        Subject subject = parser.parse(result, alertJson);
        result.setSubject(subject);

        Assert.notNull(subject, "subject == null");
        if(subject instanceof Camera) {

            if(((Camera) subject).getDetails() == null) {
                log.error("Camera: {}", subject);
            }
            Assert.notNull(((Camera) subject).getDetails(), "camera.details == null");
        }
        
        return result;
    }

    private Duration forToDuration(String fr) {
        String unit = fr.substring(fr.length() - 1);
        long value = Long.parseLong(fr.substring(0, fr.length() - 1));
        long multiply;
        switch (unit) {
            case "s" :
                multiply = 1;
                break;
            case "m" :
                multiply = 60;
                break;
            case "h" :
                multiply = 3600;
                break;
            case "d" :
                multiply = 3600*24;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + fr);
        }
        return Duration.ofSeconds(value * multiply);
    }
}
