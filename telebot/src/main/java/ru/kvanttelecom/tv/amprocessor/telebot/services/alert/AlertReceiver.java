package ru.kvanttelecom.tv.amprocessor.telebot.services.alert;

import com.hazelcast.topic.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertsFormatter;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.MarkupDetails;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.MarkupTypeEnum;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.messages.BaseMessage;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.broker.AlertMessageBusService;
import ru.kvanttelecom.tv.amprocessor.telebot.services.telegram.Telebot;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
public class AlertReceiver extends AlertMessageBusService {

    @Autowired
    private Telebot telebot;


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
        String s = AlertsFormatter.toString(alerts, MarkupTypeEnum.TELEGRAM, MarkupDetails.NONE);
        telebot.sendMessageToGroup(s);
    }




}


