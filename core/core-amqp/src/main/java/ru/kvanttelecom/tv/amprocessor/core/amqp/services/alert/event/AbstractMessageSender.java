package ru.kvanttelecom.tv.amprocessor.core.amqp.services.alert.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public abstract class AbstractMessageSender<T> {

    protected String exchanger;
    //protected String routing;

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private ObjectMapper mapper;

    public void send(T list) {
        log.debug("SENDING MESSAGE: {}", list);

        // empty key - fanout: send to all
        template.convertAndSend(exchanger, "", list);

        //template.send(exchanger, routing, new Message(bytes));
        log.debug("MESSAGE SEND");
    }
}
