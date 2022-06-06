package ru.kvanttelecom.tv.amprocessor.alerthandler.services.alert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class FiringAlertsScheduler {

    @Autowired
    AlertBroker alertBroker;


    @Scheduled(fixedDelay = 1 * 60000, initialDelay = 0 * 1000)
    public void getFiringAlerts() {
        alertBroker.downloadFiringAlerts();
    }

}
