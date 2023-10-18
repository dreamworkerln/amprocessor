package ru.kvanttelecom.tv.amprocessor.core.data.alert;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.util.Assert;
import ru.dreamworkerln.spring.utils.common.StringUtilsEx;

import ru.kvanttelecom.tv.amprocessor.core.data.organisation.OrganisationType;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.DefaultSubject;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.core.dto.cameradetails.CameraGroup;


import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.*;
import static ru.kvanttelecom.tv.amprocessor.utils.constants.Common.INSTANT_ZERO;

/**
 * Format List<Alert> to human-readable  list,
 * grouped by alert status, alert name, (if camera) camera.organisation
 */
@Slf4j
public class AlertsFormatter {

    public static final DateTimeFormatter formatterLocal =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.forLanguageTag("ru-RU"))
                    .withZone(ZoneId.systemDefault());


    /**
     * Format List<Alert> to human-readable text
     * @param alerts List<Alert>
     * @param type MarkupTypeEnum
     * @return
     */
    public static String toString(List<Alert> alerts,
                                  MarkupTypeEnum type,
                                  Set<MarkupDetails> custom) {    //, Set<MarkupSybType>

        StringBuilder sb = new StringBuilder();

        // 1. GROUP BY Alert STATUS

        Map<AlertStatus, List<Alert>> groupedByAlertStatus = alerts.stream()
                .collect(groupingBy(Alert::getStatus));

        formatAlertStatusMap(groupedByAlertStatus, type, custom, sb);

        return sb.toString();
    }




    private static void formatAlertStatusMap(Map<AlertStatus, List<Alert>> groupedByAlertStatus,
                                             MarkupTypeEnum type,
                                             Set<MarkupDetails> custom,
                                             StringBuilder sb) {

        String STATUS_DELIMITER = "\n";
        String BOLDER = "";

        switch (type) {

            case MAIL:
                STATUS_DELIMITER = "============================================\n";
                BOLDER = "";
                break;

            case TELEGRAM:

                STATUS_DELIMITER = "=============================\n";
                BOLDER = "*";
                break;
        }


        for (Map.Entry<AlertStatus, List<Alert>> e : groupedByAlertStatus.entrySet()) {

            AlertStatus status = e.getKey();
            List<Alert> statusMap = e.getValue();

            if (statusMap.size() > 0) {

                sb.append(STATUS_DELIMITER);
                String statusStr = status == AlertStatus.FIRING ?
                        "🔥 " + BOLDER + status.toString().toUpperCase() + BOLDER + " 🔥" : status.toString();
                sb.append("Alerts " + statusStr + "\n");
                sb.append(STATUS_DELIMITER);
                sb.append("\n");


                // 2. GROUP BY Alert name
                Map<String, List<Alert>> groupedByAlertName = statusMap.stream()
                        .collect(groupingBy(Alert::getName));

                formatAlertNameMap(groupedByAlertName, type, custom, sb);

            }
        }

    }


    private static void formatAlertNameMap(Map<String, List<Alert>> groupedByAlertName,
                                           MarkupTypeEnum type,
                                           Set<MarkupDetails> custom,
                                           StringBuilder sb) {

        String ALERT_DELIMITER = "\n";
        String BOLDER = "";

        switch (type) {

            case MAIL:
                ALERT_DELIMITER = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
                BOLDER = "***";
                break;

            case TELEGRAM:
                ALERT_DELIMITER = "~~~~~~~~~~~~~~~~~~~~~~~\n";
                BOLDER = "*";
                break;
        }


        for (Map.Entry<String, List<Alert>> e : groupedByAlertName.entrySet()) {

            String name = e.getKey();
            List<Alert> nameList = e.getValue();

            if(nameList.size() > 0) {
                sb.append(ALERT_DELIMITER);
                sb.append(BOLDER + name + BOLDER + "\n");
                sb.append(ALERT_DELIMITER);
                sb.append("\n");


                // 3. GROUP BY Organisation name
                Map<String, List<Alert>> groupedByAlertOrganisation = nameList.stream()
                        .collect(groupingBy(alert -> {
                            // default organisation name
                            String result = null;
                            Subject subject = alert.getSubject();
                            if (subject instanceof Camera) {
                                result = ((Camera)subject).getOrganization();
                                Assert.notNull(result, "(Subject as Camera).organisation == null");
                            }
                            else {
                                result = OrganisationType.EMPTY;
                            }

                            return result;
                        }));

                formatAlertOrganisationMap(groupedByAlertOrganisation, type, custom, sb);
            }
        }
    }



    private static void formatAlertOrganisationMap(Map<String, List<Alert>> groupedByAlertOrganisation,
                                                   MarkupTypeEnum type,
                                                   Set<MarkupDetails> custom,
                                                   StringBuilder sb) {

        String ORGANISATION_FORMATTER = "Организация: {}\n";

        switch (type) {

            case MAIL:
                ORGANISATION_FORMATTER = "--------------------------------------------\n" +
                        "Организация: {}\n" +
                        "-----------\n" +
                        "\n";
                break;


            case TELEGRAM:
                ORGANISATION_FORMATTER = "-------------------------------\n" +
                        "Организация: *{}*\n" +
                        "\n";
                break;
        }


        for (Map.Entry<String, List<Alert>> org : groupedByAlertOrganisation.entrySet()) {

            String orgName = org.getKey();
            List<Alert> alerts = org.getValue();

            if(alerts.size() > 0) {


                if(!orgName.equals(OrganisationType.EMPTY)) {
                    // organisation header
                    sb.append(formatMsg(ORGANISATION_FORMATTER, orgName));
                }
                else {
                    sb.append("\n");
                }


                // alert list
                for (Alert alert : alerts) {
                    formatAlert(alert, type, custom, sb);
                }
            }
        }
    }




//    public static final DateTimeFormatter formatterLocal =
//        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
//            .withLocale(Locale.forLanguageTag("ru-RU"))
//            .withZone(ZoneId.of("Europe/Moscow"));  // ZoneId.systemDefault()


    public static void formatAlert(Alert alert,MarkupTypeEnum type,
                                   Set<MarkupDetails> custom,
                                   StringBuilder sb) {


        switch (type) {
            case MAIL:
                break;
            case TELEGRAM:
                break;
        }




        Subject subject = alert.getSubject();
        if(alert.getSubject() instanceof Camera) {
            formatCamera((Camera)subject, type, custom, sb);
        }
        else {
            formatDefaultSubject((DefaultSubject) subject, type, custom, sb);
        }

        Instant startsAt = alert.startsAt;
        Instant endsAt = alert.endsAt;
        Duration duration = alert.duration;

        sb.append("Начало: " + formatterLocal.format(startsAt) + "\n");

        if(!endsAt.equals(INSTANT_ZERO)) {
            sb.append("Окончание: " + formatterLocal.format(endsAt) + "\n");
        }

        // add alert duration (время выставления тревоги не учитывается)
        if(!duration.equals(Duration.ZERO)) {
            sb.append("Продолжительность: " + formatDuration(duration) + "\n");
        }
        sb.append("\n");
    }




    public static void formatCamera(Camera camera, MarkupTypeEnum type,
                                    Set<MarkupDetails> custom,
                                    StringBuilder sb) {

        String BOLDER = "";

        switch (type) {

            case REDMINE:
            case MAIL:
                break;

            case TELEGRAM:
                BOLDER = "*";
                break;
        }

        String title = camera.getTitle();
        String organization = camera.getOrganization();
        Long agentId = camera.getAgentId();
        String streamUrl = camera.getStreamUrl();
        Map<String,String> referenceUrlMap = camera.getReferenceUrlMap();
        Set<CameraGroup> groups = camera.getDetails().getGroups();



        sb.append(BOLDER + title + BOLDER + "\n");
        
        // CAMERAS SHORT FORM (no watcher, mediaserver URLs)
        if(custom.contains(MarkupDetails.CAMERAS_SHORT_FORM)) {
            sb.append( camera.getName() + "\n");
            return;
        }


        if(agentId != null) {
            sb.append("Агент: ON"+ '\n');
        }
        else if (notBlank(streamUrl)) {
            String streamIP = "CANT PARSE URL";
            try {
                String u = streamUrl;
                URI uri = new URI(u);
                streamIP = uri.getHost();

            }
            catch (Exception skip) {
                String message = formatMsg("Can't parse URL");
                log.warn(message, skip);
            }
            sb.append("IP: "+ streamIP + "\n");
        }

        // referenceURL
        referenceUrlMap.forEach((k, v) -> sb.append(k + ":\n" + v + "\n"));

        sb.append("Groups: ");
        if(type.equals(MarkupTypeEnum.TELEGRAM)) {
            sb.append("\\");
        }
        sb.append("[");

        String space = "";
        for (CameraGroup gr : groups) {
            sb.append(space + gr);
            space = ", ";
        }
        sb.append("]\n");
    }


    public static void formatDefaultSubject(DefaultSubject defSubj, MarkupTypeEnum type,
                                            Set<MarkupDetails> custom,
                                            StringBuilder sb) {

        String BOLDER = "";

        switch (type) {

            case REDMINE:
            case MAIL:
                break;

            case TELEGRAM:
                BOLDER = "*";
                break;
        }

        String title = defSubj.getTitle();
        Map<String,String> referenceUrlMap = defSubj.getReferenceUrlMap();



        sb.append(BOLDER + title + BOLDER + "\n");

        // referenceURL
        if(referenceUrlMap.size() > 0) {
            referenceUrlMap.forEach((k, v) -> sb.append(k + ":\n" + v + "\n"));
        }
    }



    // ----------------------------------------------------------------------


    private static String formatDuration(Duration d) {

        String result;
        if(d.toDays() == 0) {
            result = DurationFormatUtils.formatDuration(d.toMillis(),
                    "HH:mm:ss", true);
        }
        else {
            //String days = d.toDays() == 1 ? "день" : "дней";
            String days = "дни";
            String format = StringUtilsEx.formatMsg("d '{}' HH:mm:ss", days);
            result = DurationFormatUtils.formatDuration(d.toMillis(), format, true);
        }

        return result;
    }

}
