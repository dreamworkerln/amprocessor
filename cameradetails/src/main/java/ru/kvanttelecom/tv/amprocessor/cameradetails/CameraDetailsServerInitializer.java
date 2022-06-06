package ru.kvanttelecom.tv.amprocessor.cameradetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.ModulesService;
import ru.kvanttelecom.tv.amprocessor.utils.startuprunner.BaseStartupRunner;


@Component
@Slf4j
public class CameraDetailsServerInitializer extends BaseStartupRunner {

    @Autowired
    private ModulesService modulesService;

    @Autowired
    private ObjectMapper mapper;

    @SneakyThrows
    @Override
    public void run(ApplicationArguments args) {
        super.run(args);

        // DB is up, tell that we are ready to receive data


        // MODULE IS READY: CAMERADETAILS  ==============
        modulesService.ready();
        // ==============================================
    }
}


        /*
        Message<Set<String>> msg = new Message<>(UUID.randomUUID(), new HashSet<>(Set.of("1", "2", "3")));

        String json = mapper.writeValueAsString(msg);
        log.debug("json: {}", json);

        TypeReference<Message<Set<String>>> typeReference = new TypeReference<>() {};
        Message<Set<String>> obj = mapper.readValue(json, typeReference);
        log.debug("obj: {}", obj);

        Subject subj = new DefaultSubject("name", "title", null);
        json = mapper.writeValueAsString(subj);
        log.debug("json: {}", json);
        Subject obj2 = mapper.readValue(json, Subject.class);
        log.debug("obj: {}", obj2);
        */
