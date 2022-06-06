package ru.kvanttelecom.tv.amprocessor.alerthandler.services.amqp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.amqp.services.alert.event.AlertMessageSender;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;

import java.util.List;

@Service
public class AmqpService {

    @Autowired
    AlertMessageSender messageSender;

    public void sendAlerts(List<Alert> alerts) {
        messageSender.send(alerts);
    }
}
