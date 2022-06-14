package ru.kvanttelecom.tv.amprocessor.mailer.services.alert;

import com.hazelcast.topic.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.messages.BaseMessage;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.broker.AlertMessageBusService;
import ru.kvanttelecom.tv.amprocessor.mailer.services.mail.MailFilter;

import javax.annotation.PostConstruct;
import java.util.List;


@Service
@Slf4j
public class AlertReceiver extends AlertMessageBusService {

    @Autowired
    MailFilter mailFilter;


    @PostConstruct
    private void init() {
        // add Alert listener
        addResponseHandler(this::handle);
    }

    /**
     * Handling incoming alerts
     */
    private void handle(Message<BaseMessage<List<Alert>>> message) {

        BaseMessage<List<Alert>> container = message.getMessageObject();
        int id = container.id;
        Assert.isTrue(id == -1, "Received message with id: " + id + " != -1 (multicast) ");
        List<Alert> alerts = container.body;

        log.debug("RECEIVED ALERT: {}", alerts);
        mailFilter.filterAndSend(alerts);
    }




}


