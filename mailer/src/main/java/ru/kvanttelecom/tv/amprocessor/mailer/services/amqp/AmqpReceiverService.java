package ru.kvanttelecom.tv.amprocessor.mailer.services.amqp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.amqp.AmqpIds;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertsFormatter;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.MarkupTypeEnum;
import ru.kvanttelecom.tv.amprocessor.mailer.services.mail.MailFilter;
import ru.kvanttelecom.tv.amprocessor.mailer.services.mail.MailSender;

import java.util.List;


@Service
@Slf4j
public class AmqpReceiverService {

    @Autowired
    MailFilter mailFilter;

    @Autowired
    Queue queueAlertsNew;

    //@RabbitListener(queues = AmqpIds.queue.alert.msg.new_alert, id = "new_alerts")
    @RabbitListener(queues = "#{queueAlertsNew.name}", id = "new_alerts")
    private void receive(List<Alert> alerts) {

        log.debug("RECEIVED ALERT: {}", alerts);
        mailFilter.filterAndSend(alerts);
    }
}
