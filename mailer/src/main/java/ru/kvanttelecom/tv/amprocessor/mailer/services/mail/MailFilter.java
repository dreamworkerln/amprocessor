package ru.kvanttelecom.tv.amprocessor.mailer.services.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.*;
import ru.kvanttelecom.tv.amprocessor.mailer.configurations.properties.MailProperties;
import ru.kvanttelecom.tv.amprocessor.mailer.data.mail.filtres._base.AlertFilter;

import java.beans.Introspector;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MailFilter {

    @Autowired
    ApplicationContext ctx;

    @Autowired
    MailProperties props;

    @Autowired
    private MailSender mailSender;


    public void filterAndSend(List<Alert> alerts) {


        props.getRecipients().forEach((to, filterName) -> {

            Predicate<Alert> filter = getFilter(filterName);
            List<Alert> filtered = alerts.stream().filter(filter).collect(Collectors.toList());

            log.debug("Recipient: {}, filtered alert: {}", to, filtered);

            String text = AlertsFormatter.toString(filtered, MarkupTypeEnum.MAIL, MarkupDetails.NONE);
            String subject = "Alertmanager cam: " + getSubject(filtered);

            mailSender.send(subject, to, text);
        });
    }

    /**
     * Return Filter.Predicate by filter class name
     * @param filterName
     * @return
     */
    private Predicate<Alert> getFilter(String filterName) {
         return ctx.getBean(Introspector.decapitalize(filterName), AlertFilter.class).predicate();
    }


    private String getSubject(List<Alert> alerts) {

        String result = "UNKNOWN";

        boolean firing = false;
        boolean resolved = false;

        for (Alert alert : alerts) {
            firing = alert.getStatus() == AlertStatus.FIRING;
            resolved = alert.getStatus() == AlertStatus.RESOLVED;
        }

        if(firing && !resolved) {
            result = AlertStatus.FIRING.toString();
        }
        else if (!firing && resolved) {
            result = AlertStatus.RESOLVED.toString();
        }
        else {
            result = "MIXED";
        }

        return result;
    }


}


