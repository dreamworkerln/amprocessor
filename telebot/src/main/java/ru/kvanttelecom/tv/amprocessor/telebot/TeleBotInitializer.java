package ru.kvanttelecom.tv.amprocessor.telebot;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.ModulesService;
import ru.kvanttelecom.tv.amprocessor.utils.startuprunner.BaseStartupRunner;

@Component
@Slf4j
public class TeleBotInitializer extends BaseStartupRunner {

    @Autowired
    ModulesService modulesService;

    @Override
    public void run(ApplicationArguments args) {
        super.run(args);

        // MODULE IS READY: TELEBOT  ==============
        modulesService.ready();
        // =======================================
    }
}
