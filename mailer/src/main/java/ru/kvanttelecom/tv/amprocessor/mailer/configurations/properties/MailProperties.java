package ru.kvanttelecom.tv.amprocessor.mailer.configurations.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MailProperties {

    @Value("${mail.username}")
    @Getter
    private String username;

    @Value("${mail.password}")
    @Getter
    private String password;

    @Value("${mail.host}")
    @Getter
    private String host;

    @Value("${mail.port}")
    @Getter
    private Integer port;

    @Value("${mail.transport.protocol}")
    @Getter
    private String transportProtocol;

    @Value("${mail.smtp.auth}")
    @Getter
    private String smtpAuth;

    @Value("${mail.smtp.starttls.enable}")
    @Getter
    private String startTlsEnable;

    //@Value("${mail.to}")
    @Value("#{${mail.recipients}}")
    @Getter
    private Map<String,String> recipients;


    @Value("${mail.wait.interval}")
    @Getter
    private Integer waitIntervalBetweenSendSec;
}
