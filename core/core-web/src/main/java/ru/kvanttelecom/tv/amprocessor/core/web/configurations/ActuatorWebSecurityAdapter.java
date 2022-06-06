package ru.kvanttelecom.tv.amprocessor.core.web.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.annotation.PostConstruct;

@Configuration
@Order(-1)
@EnableWebSecurity
@Slf4j
public class ActuatorWebSecurityAdapter extends BasicWebSecurityAdapter {

    public final static String ACTUATOR = "ACTUATOR";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        baseHttpSecurity(http);
        http.authorizeRequests().mvcMatchers("/actuator/**").hasRole(ACTUATOR);
        http.authorizeRequests().anyRequest().authenticated();
    }

    @PostConstruct
    private void init() {

        // Get user credential details from application.properties
        UserDetails userDetails = User.withUsername(env.getProperty("management.security.user.name"))
            .password(passwordEncoder.encode(env.getProperty("management.security.user.password")))
            .roles("ACTUATOR")
            .build();
        userManager.createUser(userDetails);
    }


}
