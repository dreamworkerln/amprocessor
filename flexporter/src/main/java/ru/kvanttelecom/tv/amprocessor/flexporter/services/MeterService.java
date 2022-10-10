package ru.kvanttelecom.tv.amprocessor.flexporter.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.flexporter.data.CameraState;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static ru.kvanttelecom.tv.amprocessor.core.prometheus.data.schema.prometheus_series.flussonic.*;

/**
 * Service to work with Micrometer meters
 */
@Service
@Slf4j
public class MeterService {

    @Autowired
    CameraStateService stateService;

    @Autowired
    MeterRegistry registry;

    private final Map<String, Counter> counterMap = new ConcurrentHashMap<>();

    // ручное черезжопное внедрение зависимости, обход цикла зависимостей
    @PostConstruct
    private void init() {
        stateService.setMeterService(this);
    }


    /**
     * Create new gauges(meter) for
     * <br>
     * camera.alive and camera.flapping.frequency
     */
    public void addMetrics(Camera camera) {


        String name = camera.getName();

        //Assert.notNull(state, formatMsg("Camera {} == null", camera));


        // stream.alive gauge
        Gauge.builder(metrics.stream.alive.name, name, s -> {  // s == name
                CameraState state = stateService.get(s);
                return state.isAlive() ? 1 : 0;
            })
            .tags(tags.host, camera.getHostname(), tags.stream, name, tags.title, camera.getTitle())
            .description(metrics.stream.alive.description)
            .register(registry);

//        // camera.flapping.frequency gauge
//        Gauge.builder(metrics.stream.flapping.frequency.name, name, s -> { // s == name
//                CameraState state = stateService.get(s);
//                return state.getFrequency();
//            })
//            .tags(tags.stream, name, tags.title, camera.getTitle())
//            .description(metrics.stream.flapping.frequency.description)
//            .register(registry);


        // camera.flapping.downcount counter
        Counter counter =
        Counter.builder(metrics.stream.flapping.down.name)
            .tags(tags.host, camera.getHostname(), tags.stream, name, tags.title, camera.getTitle())
            .description(metrics.stream.flapping.down.description)
            .register(registry);
        counterMap.put(camera.getName(), counter);
    }



    /**
     * Remove existing stream metrics
     * @param name Stream name
     */
    public void removeMetrics(String name) {

        //Assert.notNull(state, formatMsg("Stream {} == null", key));
        //Assert.notNull(state, formatMsg("StreamState {} == null", state));

        Gauge gauge = registry.get(metrics.stream.alive.name).tag(tags.stream, name).gauge();
        registry.remove(gauge);

//        gauge = registry.get(metrics.stream.flapping.frequency.name).tag(tags.stream, name).gauge();
//        registry.remove(gauge);

        Counter counter = registry.get(metrics.stream.flapping.down.name).tag(tags.stream, name).counter();
        registry.remove(counter);
        counterMap.remove(name);
    }

    // ----------------------------------------------------------------------------

    /**
     * Increase Stream downcount counter
     * @param name Stream name
     */
    public void incCounter(String name) {

        Counter counter = counterMap.get(name);
        if(counter != null) {
            counter.increment();
        }
    }

    // ============================================================================
}

//    private boolean existsMeterTitle(StreamKey key) {
//
//        boolean result = false;
//        String title = streamService.get(key).getTitle();
//
//        try {
//            // throw exception if not found on .gauge()
//            meterRegistry.get("stream.alive").tag("title", title).gauge();
//            meterRegistry.get("stream.flapping.frequency").tag("title", title).gauge();
//            result = true;
//        }
//        catch (Exception ignore){}
//
//        return result;
//    }

