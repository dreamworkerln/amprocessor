package ru.kvanttelecom.tv.amprocessor.flexporter.services;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.core.dto.camera.Camera;
import ru.kvanttelecom.tv.amprocessor.flexporter.data.CameraState;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.formatMsg;


/**
 * Store StreamState for all streams
 */
@Service
@Slf4j
public class CameraStateService {

    // ручное черезжопное внедрение зависимости, обход цикла зависимостей
    @Setter
    private MeterService meterService;

    // Storage of cameras states (calculated)
    private final ConcurrentMap<String, CameraState> states = new ConcurrentHashMap<>();

//    public StreamStateService() {
//        super.map = states;
//    }

    public void create(Camera camera) {
        CameraState state = new CameraState(camera);
        states.put(camera.getName(), state);
    }


    /**
     * Update camera state (set aliveness and calculate frequency)
     * @param key StreamKey
     * @param update new camera state (up/down)
     */
    public void update(String name, boolean alive) {



        //StreamState state = states.computeIfAbsent(key, streamKey -> {new StreamState()});

        // get existed CameraState
        CameraState state = states.get(name);
        Assert.notNull(state, formatMsg("StreamState {} == null", name));

        Instant now = Instant.now();

        // фронт возрастание, camera goes online  ---------  << ТУТ НИЧЕГО НЕ ДЕЛАЕМ, ЭТО ВЫЗОВЕТСЯ ПРИ ПЕРВОЙ ИНИЦИАЛИЗАЦИИ StreamState
        if(alive && !state.isAlive()) {
            log.debug("Camera {} goes online", name);
        }

        // фронт падение, camera goes offline -------------   << ТУТ ДЕЛАЕМ ОТСЧЕТ ЧАСТОТЫ, ПО ПАДАЮЩЕМУ ФРОНТУ
        if(!alive && state.isAlive()) {
            log.debug("Camera {} goes offline", name);
            // делаем отсчет
            state.goDown();
        }

        // state not changed ------------------------------

        // save new alive value
        state.setAlive(alive);
    }

    public Set<String> keySet() {
        return states.keySet();
    }
    
    public CameraState remove(String key) {
        return states.remove(key);
    }

    public boolean containsKey(String key) {
        return states.containsKey(key);
    }

    public CameraState get(String key) {
        return states.get(key);
    }



    /**
     * Update StreamState.Camera
     * <br>
     * Used when Camera title/other fields was changed
     * @param name old Camera.name
     * @param camera new Camera
     */
    public void updateCamera(String name, Camera camera) {

        Assert.notNull(states.get(name), "Can't find by key " + name);
        Assert.isTrue(camera.getName().equals(name), "Bad camera name "  + name);

        CameraState state = states.get(name);
        state.setCamera(camera);
    }

}
