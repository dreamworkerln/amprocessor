package ru.kvanttelecom.tv.amprocessor.alerthandler.services.alert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.alerthandler.services.amqp.AmqpService;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.AlertService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.ModulesService;

import java.util.List;

@Slf4j
@Service
public class AlertBroker {

    @Autowired
    private AlertService alertService;

    @Autowired
    private AmqpService amqpService;

    @Autowired
    private AlertParser parser;

    @Autowired
    AlertFiringDownloader alertFiringDownloader;

    @Autowired
    ModulesService modulesService;

    /**
     * Consume alertmanager push notifications
     * @param json
     */
    public void sinkNewAlerts(String json) {


        // parse alerts to objects
        List<Alert> newAlerts = parser.parse(json);

        log.debug("Got new alerts: {}", newAlerts);

        // aggregate alerts to StatusAlertMap
        // newAlerts.add(alerts);

        // immediately send alerts to AMQP fanout exchanger
        amqpService.sendAlerts(newAlerts);

    }

//    public List<Alert> getAlertsFiring() {
//        return currentAlerts;
//    }


    /**
     * Get currently firing alerts
     */
    public void downloadFiringAlerts() {

        //log.debug("Requesting current firing alerts");
        // get current firing alerts from prometheus
        List<Alert> currentFiringAlerts =  alertFiringDownloader.getFiring();

        // save all firing alerts to hazelcast
        alertService.clearAndSet(currentFiringAlerts);

        // MODULE IS READY: ALERTHANDLER  ===============
        modulesService.ready();
        // ==============================================
    }

}


//        try {
//            ResponseEntity<String> resp = restClient.get(
//                promProps.getProtocol() +
//                promProps.getAddress() +
//                PrometheusProperties.REST_API_GET_ALERTS_PATH);
//
//            if(resp.hasBody()) {
//                List<Alert> currentFiringAlerts = parser.parse(resp.getBody());
//                currentFiringAlerts.removeIf(a -> a.getStatus() != AlertStatus.FIRING);
//                // save all firing alerts to hazelcast
//                alertsFiringCache.set(currentFiringAlerts);
//            }
//        }
//        catch (Exception skip) {
//            log.error("Get current firing alerts error: ", skip);
//        }

//System.out.println(Arrays.toString(alertsFiringCache.get().toArray()));
