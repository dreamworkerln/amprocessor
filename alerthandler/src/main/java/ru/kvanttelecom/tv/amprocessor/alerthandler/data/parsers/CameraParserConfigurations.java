package ru.kvanttelecom.tv.amprocessor.alerthandler.data.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.cameras.CameraService;

import static ru.kvanttelecom.tv.amprocessor.core.data.alert.AlertName.STREAM_DOWN;


// StreamDown alerts

@Configuration
public class CameraParserConfigurations {

    @Autowired
    private CameraService cameraService;



    @Bean(STREAM_DOWN)
    CameraParser getStreamParser() {
        return new CameraParser(STREAM_DOWN, cameraService);
    }

//    @Bean(STREAM_DOWN_TEST)
//    StreamParser getStreamParserTest() {
//        return new StreamParser(STREAM_DOWN_TEST, cache);
//    }
}
