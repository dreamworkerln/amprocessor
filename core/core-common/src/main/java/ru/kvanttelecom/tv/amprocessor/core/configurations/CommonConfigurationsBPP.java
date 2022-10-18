package ru.kvanttelecom.tv.amprocessor.core.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dreamworkerln.spring.utils.common.bpp.InitializerBPP;

@Configuration
public class CommonConfigurationsBPP {

        // use Ordered interface to set BeanPostProcessor load order
        @Bean
        public InitializerBPP initializerBPP() {
            return new InitializerBPP();
        }
}
