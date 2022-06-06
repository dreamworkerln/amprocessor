package ru.kvanttelecom.tv.amprocessor.sgrabber;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Properties;

import static ru.kvanttelecom.tv.amprocessor.core.configurations.CommonConfigurations.PROJECT_PACKAGE_NAME;
import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.printHeapAndCloudConfConnecting;

@SpringBootApplication(scanBasePackages = PROJECT_PACKAGE_NAME)
public class StreamGrabberApplication {

    public static void main(String[] args) {
        printHeapAndCloudConfConnecting();

        ConfigurableApplicationContext app = SpringApplication.run(StreamGrabberApplication.class, args);
    }
}
