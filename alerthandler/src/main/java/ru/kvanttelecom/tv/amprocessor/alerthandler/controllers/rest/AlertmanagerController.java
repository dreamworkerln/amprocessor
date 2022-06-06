package ru.kvanttelecom.tv.amprocessor.alerthandler.controllers.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.kvanttelecom.tv.amprocessor.alerthandler.services.alert.AlertBroker;

@RestController
@Slf4j
public class AlertmanagerController {

    @Autowired
    private AlertBroker alertBroker;

    @PostMapping("/alertmanager_events")
    public void alertHandler(@RequestBody String json) {
        try {
            log.debug("Received: {}", json.trim());
            alertBroker.sinkNewAlerts(json);
        }
        catch (Exception rethrow) {
            log.error("Internal server error:", rethrow);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
