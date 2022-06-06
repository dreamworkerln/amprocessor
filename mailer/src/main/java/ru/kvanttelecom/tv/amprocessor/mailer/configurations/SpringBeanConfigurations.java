package ru.kvanttelecom.tv.amprocessor.mailer.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.kvanttelecom.tv.amprocessor.mailer.configurations.properties.MailProperties;

@Configuration
public class SpringBeanConfigurations {

    @Autowired
    MailProperties mailProps;


    @Bean
    public JavaMailSender getJavaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProps.getHost());
        mailSender.setPort(mailProps.getPort());

        mailSender.setUsername(mailProps.getUsername());
        mailSender.setPassword(mailProps.getPassword());

        //props.put("mail.transport.protocol", "smtp");
        //props.put("mail.smtp.auth", "true");
        //props.put("mail.smtp.starttls.enable", "true");
        //props.put("mail.debug", "true");

        return mailSender;
    }
}
