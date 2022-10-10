package ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers.camera.SyntheticCameraParser;
import ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers.camera.MediaServerCameraParser;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;

import static ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertName.STREAM_DOWN;
import static ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertName.STREAM_FLAPPING;


// StreamDown alerts

@Configuration
public class CameraParserConfigurations {

    @Autowired
    private CameraService cameraService;



    @Bean(STREAM_DOWN)
    MediaServerCameraParser getStreamParser() {
        return new MediaServerCameraParser(STREAM_DOWN, cameraService);
    }

    @Bean(STREAM_FLAPPING)
    SyntheticCameraParser getStreamFlapping() {
        return new SyntheticCameraParser(STREAM_FLAPPING, cameraService);
    }

//    @Bean(STREAM_DOWN_TEST)
//    StreamParser getStreamParserTest() {
//        return new StreamParser(STREAM_DOWN_TEST, cache);
//    }
}
