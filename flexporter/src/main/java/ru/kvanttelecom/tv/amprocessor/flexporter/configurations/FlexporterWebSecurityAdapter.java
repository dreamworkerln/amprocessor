package ru.kvanttelecom.tv.amprocessor.flexporter.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import ru.kvanttelecom.tv.amprocessor.core.web.configurations.BasicWebSecurityAdapter;


import javax.annotation.PostConstruct;

import static ru.kvanttelecom.tv.amprocessor.core.web.configurations.ActuatorWebSecurityAdapter.baseHttpSecurity;


@Configuration
@Order(-2)    // -2 - OVERRIDING ORDER, before ActuatorWebSecurityAdapter (-1) !!!!
@Slf4j
public class FlexporterWebSecurityAdapter extends BasicWebSecurityAdapter {

    public static final String PROMETHEUS = "PROMETHEUS";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        baseHttpSecurity(http);
        http.authorizeRequests().mvcMatchers("/actuator/prometheus").hasRole(PROMETHEUS);
        http.authorizeRequests().anyRequest().authenticated();
    }


    @PostConstruct
    private void init() {
        UserDetails userDetails = User.withUsername(env.getProperty("spring.security.user.name"))
            .password(passwordEncoder.encode(env.getProperty("spring.security.user.password")))
            .roles(PROMETHEUS)
            .build();
        userManager.createUser(userDetails);
    }
}