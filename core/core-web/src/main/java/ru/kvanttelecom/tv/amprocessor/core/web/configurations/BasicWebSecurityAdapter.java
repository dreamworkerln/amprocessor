package ru.kvanttelecom.tv.amprocessor.core.web.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@EnableWebSecurity
@Slf4j
public abstract class BasicWebSecurityAdapter extends WebSecurityConfigurerAdapter {
    @Autowired
    protected Environment env;
    @Autowired
    protected UserDetailsManager userManager;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    public static void baseHttpSecurity(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().disable();
        http.formLogin().disable();
        http.httpBasic();
    }

    @Override
    public UserDetailsService userDetailsService() {
        return userManager;
    }
}
