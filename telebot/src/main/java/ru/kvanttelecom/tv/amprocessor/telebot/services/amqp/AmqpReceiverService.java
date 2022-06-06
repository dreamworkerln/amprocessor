package ru.kvanttelecom.tv.amprocessor.telebot.services.amqp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.amqp.AmqpIds;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertsFormatter;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.MarkupTypeEnum;
import ru.kvanttelecom.tv.amprocessor.telebot.services.telegram.Telebot;

import java.util.List;


@Service
@Slf4j
public class AmqpReceiverService {

    @Autowired
    private Telebot telebot;

    @Autowired
    Queue queueAlertsNew;

    //@RabbitListener(queues = AmqpIds.queue.alert.msg.new_alert, id = "new_alerts"/*, autoStartup = "false"*/)
    @RabbitListener(queues = "#{queueAlertsNew.name}", id = "new_alerts")
    private void receive(List<Alert> alerts) {

        //List<Alert> alerts = mapper.readValue(bytes, new TypeReference<>(){});

        log.debug("RECEIVED ALERT: {}", alerts);

        String s = AlertsFormatter.toString(alerts, MarkupTypeEnum.TELEGRAM);
        telebot.sendMessageToGroup(s);
    }

//    @RabbitListener(queues = AmqpIds.queue.alert.msg.new_alert, id = "new_alerts"/*, autoStartup = "false"*/)
//    private void receive(List<Alert> alerts) {
//
//        log.debug("CAMERA EVENT: {}", alerts);
//
//        String s = AlertsFormatter.toString(alerts, MarkupTypeEnum.TELEGRAM);
//        telebot.sendMessageToGroup(s);
//    }



}
