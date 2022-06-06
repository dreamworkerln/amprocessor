package ru.kvanttelecom.tv.amprocessor.core.telebot.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BaseBotTest {

    @Test
    void postConstruct() {
        System.out.println("Hi");
    }
}