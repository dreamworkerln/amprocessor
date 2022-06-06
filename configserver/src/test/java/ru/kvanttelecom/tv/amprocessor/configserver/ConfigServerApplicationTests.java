package ru.kvanttelecom.tv.amprocessor.configserver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//@SpringBootTest
@SpringBootTest(properties = {"spring.profiles.active=native,test","ass=hole"})
// will not load dev profile
//@ActiveProfiles("native,dev")
@ExtendWith(SpringExtension.class)
class ConfigServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
