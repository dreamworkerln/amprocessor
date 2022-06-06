package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionalMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.BaseCacheService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.alerts.configurations.AlertsCacheConfiguration;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.configurations.CamerasCacheConfiguration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AlertService extends BaseCacheService<Integer, Alert, Set<String>, Void> {

    @Autowired
    public void setCacheConfiguration(AlertsCacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

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

    public List<Alert> get() {
        return new ArrayList<>(getMap().values());
    }

}
