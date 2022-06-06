package ru.kvanttelecom.tv.amprocessor.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class CommonUtils {

    /**
     * Multiply/divide timeout by 2
     * @param direction
     *        multiply = direction==UP
     *        divide   = direction==DOWN
     */
    public static void calculateNewTimeout(Direction direction, AtomicReference<Duration> timeout, Duration min, Duration max) {

        timeout.getAndUpdate(d -> {

            Duration result = d;
            if (direction == Direction.UP) {
                result = d.multipliedBy(2);
            }
            else if (direction == Direction.DOWN) {
                result = d.dividedBy(2);
            }

            if (result.toSeconds() > max.toSeconds() ||
                result.toSeconds() < min.toSeconds()) {
                result = d;
            }
            return result;
        });
    }

    public static void printHeap() {
        System.out.println("Max heap: " + Runtime.getRuntime().maxMemory()/(1024*1024) +"MB");
    }

    public static void printConnectingToSpringCloudClient() {
        System.out.println("Connecting to spring-cloud-config server ...");
    }

    public static void printHeapAndCloudConfConnecting() {
        printHeap();
        printConnectingToSpringCloudClient();
    }

    /**
     * Remove port
     * @param instance
     * @return domainName
     */
    public static String getDomainName(String instance) {
        int i = instance.indexOf(":");
        return (i!=-1) ? instance.substring(0, i) : instance;
    }
}
