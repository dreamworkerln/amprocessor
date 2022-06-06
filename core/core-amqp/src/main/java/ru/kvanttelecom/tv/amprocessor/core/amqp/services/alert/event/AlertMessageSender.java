package ru.kvanttelecom.tv.amprocessor.core.amqp.services.alert.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.amqp.AmqpIds;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;

import java.util.List;

@Service
@Slf4j
public class AlertMessageSender extends AbstractMessageSender<List<Alert>> {

    public AlertMessageSender() {
        exchanger = AmqpIds.exchanger.alert.msg.new_alert;
        //routing   = AmqpIds.binding.alert.msg.new_alert;
    }



//    @SneakyThrows
//    public void send(List<Alert> alerts) {
//
//        String exchanger = AmqpIds.exchanger.alert.msg.new_alert;
//        String routing   = AmqpIds.binding.alert.msg.new_alert;
//
//        log.debug("SENDING NEW ALERTS: {}", alerts);
//
//        byte[] bytes = mapper.writeValueAsBytes(alerts);
//        //String json = mapper.writeValueAsString(alerts);
//        template.send(exchanger, routing, new Message(bytes));
//        //template.convertAndSend(exchanger, routing, json);
//        log.debug("NEW ALERTS SEND");
//    }

}
