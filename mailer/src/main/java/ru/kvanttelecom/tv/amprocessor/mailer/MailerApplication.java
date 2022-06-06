package ru.kvanttelecom.tv.amprocessor.mailer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static ru.kvanttelecom.tv.amprocessor.core.configurations.CommonConfigurations.PROJECT_PACKAGE_NAME;
import static ru.kvanttelecom.tv.amprocessor.utils.CommonUtils.printHeapAndCloudConfConnecting;

@SpringBootApplication(scanBasePackages = PROJECT_PACKAGE_NAME)
public class MailerApplication {
    public static void main(String[] args) {

        printHeapAndCloudConfConnecting();
        SpringApplication.run(MailerApplication.class, args);
    }
}
