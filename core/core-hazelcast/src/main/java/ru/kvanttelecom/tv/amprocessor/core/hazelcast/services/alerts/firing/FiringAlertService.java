package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.firing;

import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionalMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.BaseCacheService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.firing.configurations.FiringAlertCacheConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Currently Firing alerts
 */
@Service
public class FiringAlertService extends BaseCacheService<Integer, Alert, Set<String>, Void> {

    @Autowired
    public void setCacheConfiguration(FiringAlertCacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

    /**
     * Clear all alerts and set new list in transaction
     * @param list
     */
    public void clearAndSet(List<Alert> list) {

        TransactionContext context = getTransactionContext();

        // Start transaction
        context.beginTransaction();
        try {

            TransactionalMap<Integer,Alert> alerts = context.getMap(mapName);

            // Clear all
            alerts.keySet().forEach(alerts::delete);
            AtomicInteger idGen = new AtomicInteger(0);
            list.forEach(a -> alerts.put(idGen.getAndIncrement(), a));
            context.commitTransaction();
        }
        catch (Throwable rethrow) {
            context.rollbackTransaction();
            throw rethrow;
        }
    }

    /**
     * Get all currently firing alerts
     */
    public List<Alert> get() {
        return new ArrayList<>(getMap().values());
    }

}
