package ru.kvanttelecom.tv.amprocessor.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static ru.kvanttelecom.tv.amprocessor.core.configurations.CommonConfigurations.PROJECT_PACKAGE_NAME;
import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.printHeap;

@EnableConfigServer
@SpringBootApplication(scanBasePackages = PROJECT_PACKAGE_NAME)
public class ConfigServerApplication {

    public static void main(String[] args) {
        printHeap();

        SpringApplication.run(ConfigServerApplication.class, args);
    }

}

//SpringApplication springApplication = new SpringApplication(ConfigServerApplication.class);
//springApplication.addListeners(new SpringBuiltInEventsListener());
//springApplication.run(args);
//SpringApplication.run(ConfigServerApplication.class, args);
