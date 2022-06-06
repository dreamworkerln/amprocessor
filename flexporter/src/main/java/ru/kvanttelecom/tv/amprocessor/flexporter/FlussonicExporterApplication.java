package ru.kvanttelecom.tv.amprocessor.flexporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static ru.kvanttelecom.tv.amprocessor.core.configurations.CommonConfigurations.PROJECT_PACKAGE_NAME;
import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.printHeapAndCloudConfConnecting;

@SpringBootApplication(scanBasePackages = PROJECT_PACKAGE_NAME)
@Slf4j
public class FlussonicExporterApplication {

    public static void main(String[] args) {
        printHeapAndCloudConfConnecting();

        SpringApplication.run(FlussonicExporterApplication.class, args);
    }
}
