package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.broker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.BaseCacheService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.broker.configurations.AlertMessageBusConfiguration;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.firing.configurations.FiringAlertCacheConfiguration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 * Alerts message bus
 */
@Service
public class AlertMessageBusService extends BaseCacheService<Integer, Alert, Void, List<Alert>> {

    @Autowired
    public void setCacheConfiguration(AlertMessageBusConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

    /**
     * Publish new Alerts to subscribers
     * @param alerts Set<Alert>
     */
    public void publish(List<Alert> alerts) {
        // put message to responseTopic with id == -1
        reply(-1, alerts);
    }
}
