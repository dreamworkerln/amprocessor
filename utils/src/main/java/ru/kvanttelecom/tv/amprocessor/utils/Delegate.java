package ru.kvanttelecom.tv.amprocessor.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;


/**
 * Delegate
 * @param <T>
 */
@Slf4j
public class Delegate<T> {

    private final static IntUnaryOperator intCycleIncrementator = (i) -> i == Integer.MAX_VALUE ? 0 : i + 1;
    private final AtomicInteger idGen = new AtomicInteger(1);

    private final ConcurrentMap<Integer, Consumer<T>> handlerMap = new ConcurrentHashMap<>();

    /**
     * Add handler
     * @param handler handler
     * @return registration id
     */
    public int add(Consumer<T> handler) {
        int id = idGen.getAndUpdate(intCycleIncrementator);
        handlerMap.put(id, handler);
        return id;
    }

    /**
     * Remove registration
     * @param id obtained from add(...)
     */
    public void remove(int id) {
        handlerMap.remove(id);
    }

    //

    /**
     * Apply event to all listeners
     * <br>Not cloning message, better message be deep immutable
     * @param message
     */
    public void apply(T message) {
        handlerMap.forEach(
            (id, consumer) -> {
                try {
                    consumer.accept(message);
                } catch (Exception e) {
                    log.error("Delegate sendAll() error: ", e);
                }
            }
        );
    }

    /**
     * Assigned handlers size
     */
    public int size() {
        return handlerMap.size();
    }

    public boolean containsKey(int id) {
        return handlerMap.containsKey(id);
    }
}
