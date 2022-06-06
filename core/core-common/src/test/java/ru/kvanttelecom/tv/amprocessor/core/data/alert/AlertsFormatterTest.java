package ru.kvanttelecom.tv.amprocessor.core.data.alert;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.kvanttelecom.tv.amprocessor.core.data.subject.Subject;

import java.time.*;

@SpringBootTest
class AlertsFormatterTest {

    // TFD (test for debug), now disabled
    // (TFD is not test at all, it required for debugging process and is issued as test)
    @Test
    @Disabled
    void testToString() {

        LocalDate localDate = LocalDate.parse("2016-04-17");
        LocalDateTime localDateTime = localDate.atStartOfDay();

        Subject subject = null;

//        Subject subject = new Subject("test-subject-key",
//            "test-subject-name",
//            "Test Subject Title",
//            "Test Subject URL");

//        Alert alert = new Alert();
//        alert.name = "Tes Alert";
//        alert.description = "Test Alert Description";
//        alert.subject = subject;
//        alert.startsAt = localDateTime.toInstant(ZoneOffset.UTC);
//        alert.endsAt = alert.startsAt.plusSeconds(3 * 24 * 3600);
//        alert.duration = Duration.between(alert.startsAt, alert.endsAt);
//        alert.status = AlertStatus.RESOLVED;
    }
}