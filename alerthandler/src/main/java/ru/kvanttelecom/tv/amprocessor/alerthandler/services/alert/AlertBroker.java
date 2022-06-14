package ru.kvanttelecom.tv.amprocessor.alerthandler.services.alert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.broker.AlertMessageBusService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.firing.FiringAlertService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.ModulesService;

import java.util.List;

@Slf4j
@Service
public class AlertBroker {

    @Autowired
    private FiringAlertService firingAlertService;

    @Autowired
    private AlertParser parser;

    @Autowired
    private AlertFiringDownloader alertFiringDownloader;

    @Autowired
    private ModulesService modulesService;

    @Autowired
    private AlertMessageBusService alertMessageBusService;

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

        // immediately send alerts to subscribers
        alertMessageBusService.publish(newAlerts);
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
        firingAlertService.clearAndSet(currentFiringAlerts);

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
