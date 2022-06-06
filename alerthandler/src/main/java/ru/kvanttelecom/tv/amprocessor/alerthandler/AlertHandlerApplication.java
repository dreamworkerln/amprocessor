package ru.kvanttelecom.tv.amprocessor.alerthandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static ru.kvanttelecom.tv.amprocessor.core.configurations.CommonConfigurations.PROJECT_PACKAGE_NAME;
import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.printHeapAndCloudConfConnecting;

/**
 * This service
 * 1. Receive prometheus alert manager webhook push about new alerts.
 * New alert go to AMQP fanout exchanger to all subscribers
 *
 * 2. Period 60s call prometheus alert manager REST API, get all current firing alerts.
 * All currently firing alerts stored in hazelcast Map
 */
@SpringBootApplication(scanBasePackages = PROJECT_PACKAGE_NAME)
public class AlertHandlerApplication {

    public static void main(String[] args) {
        printHeapAndCloudConfConnecting();
        SpringApplication.run(AlertHandlerApplication.class, args);
    }
}
