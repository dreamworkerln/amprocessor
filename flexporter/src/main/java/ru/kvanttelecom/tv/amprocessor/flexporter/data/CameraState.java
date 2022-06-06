package ru.kvanttelecom.tv.amprocessor.flexporter.data;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import ru.kvanttelecom.tv.amprocessor.core.SpringContextConfig;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.flexporter.services.MeterService;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongUnaryOperator;

@Slf4j


/**
 * Camera state - is alive, flapping frequency
 */
public class CameraState {

    private final static LongUnaryOperator longCycleIncrementator = (i) -> i == Long.MAX_VALUE ? 0 : i + 1;

    // Is camera alive (up/down)
    private final AtomicBoolean alive = new AtomicBoolean(false);

    // отсчет падений, второй вариант вычисления частоты
    private final AtomicLong downCount = new AtomicLong(0); // used by Prometheus to calculate frequency itself

    @Getter @Setter
    private Camera camera;


    public CameraState(Camera camera) {
        this.camera = camera;
    }

    public boolean isAlive() {
        return alive.get();
    }

    public void setAlive(boolean alive) {
        this.alive.set(alive);
    }

    // обработчик отсчета (по падению фронта)
    public void goDown() {
        downCount.updateAndGet(longCycleIncrementator);

        MeterService meterService = SpringContextConfig.getBean(MeterService.class);
        meterService.incCounter(camera.getName());
    }


    @Override
    public String toString() {
        return "StreamState{" +
            ", camera=" + camera.getName() +
            ", alive=" + alive +
            ", downCount=" + downCount.get() +
            '}';
    }

}
