package ru.kvanttelecom.tv.amprocessor.core.data.alert;

import lombok.Data;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Common class for prometheus alert
 * Subject - may be gathered from other systems
 */
@Data
public class Alert implements Serializable {

    protected String name;         // alert name
    protected AlertStatus status;  // alert status
    protected String severity;     // alert severity
    protected String instance;     // alert instance (domain name+port(if any))
    protected String title;        // alert title
    protected String description;  // alert description
    protected Instant startsAt;    // start at
    protected Instant endsAt;      // end at (or now if still firing)
    protected Duration duration;   // calculated duration

    protected Subject subject;
    //protected Duration forr;         // pending duration threshold
    //
    //protected String referenceUrl; // generated data, link to moar info about alert

    public static final DateTimeFormatter formatterLocal =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.forLanguageTag("ru-RU"))
            .withZone(ZoneId.systemDefault());

//    public static final DateTimeFormatter formatterLocal =
//        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
//            .withLocale(Locale.forLanguageTag("ru-RU"))
//            .withZone(ZoneId.of("Europe/Moscow"));  // ZoneId.systemDefault()

//
//    public String toString(MarkupTypeEnum type) {
//
//
//        switch (type) {
//            case MAIL:
//                break;
//            case TELEGRAM:
//                break;
//        }
//
//
//
//        StringBuilder sb = new StringBuilder();
//        // subject
//        sb.append(subject.toString(type));
//
//        sb.append("Начало: " + formatterLocal.format(startsAt) + "\n");
//
//        if(!endsAt.equals(INSTANT_ZERO)) {
//            sb.append("Окончание: " + formatterLocal.format(endsAt) + "\n");
//        }
//
//        // add alert duration (время выставления тревоги не учитывается)
//        if(!duration.equals(Duration.ZERO)) {
//            sb.append("Продолжительность: " + formatDuration(duration) + "\n");
//        }
//
//        return sb.toString();
//    }

//
//    public static String formatDuration(Duration d) {
//
//        String result;
//        if(d.toDays() == 0) {
//            result = DurationFormatUtils.formatDuration(d.toMillis(),
//                "HH:mm:ss", true);
//        }
//        else {
//            //String days = d.toDays() == 1 ? "день" : "дней";
//            String days = "дни";
//            String format = StringUtilsEx.formatMsg("d '{}' HH:mm:ss", days);
//            result = DurationFormatUtils.formatDuration(d.toMillis(), format, true);
//        }
//
//        return result;
//    }


//    @Override
//    public String toString() {
//        return "Alert{" +
//            "name='" + name + '\'' +
//            ", status=" + status +
//            ", severity='" + severity + '\'' +
//            ", instance='" + instance + '\'' +
//            ", title='" + title + '\'' +
//            ", description='" + description + '\'' +
//            ", startsAt=" + startsAt +
//            ", endsAt=" + endsAt +
//            ", duration=" + duration +
//            '}';
//    }

}
