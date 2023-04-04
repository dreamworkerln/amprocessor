package ru.kvanttelecom.tv.amprocessor.core.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dreamworkerln.spring.utils.common.configurations.bpp.PostInitializeBPP;

@Configuration
public class CommonConfigurationsBPP {

        // use Ordered interface to set BeanPostProcessor load order
        @Bean
        public PostInitializeBPP postInitializeBPP() {
            return new PostInitializeBPP();
        }
}
