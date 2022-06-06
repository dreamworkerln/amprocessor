package ru.kvanttelecom.tv.amprocessor.telebot.configurations.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.kvanttelecom.tv.amprocessor.core.telebot.configurations.properties.TelebotProperties;


@Component
public class BotProperties implements TelebotProperties {

    @Autowired
    Environment env;

    @Value("${telegram.bot.token}")
    @Getter
    private String botToken;

    @Value("${telegram.bot.group}")
    @Getter
    private Long botGroup;
}
