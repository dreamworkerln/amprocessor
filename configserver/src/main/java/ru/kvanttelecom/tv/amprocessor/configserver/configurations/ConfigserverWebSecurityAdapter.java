package ru.kvanttelecom.tv.amprocessor.configserver.configurations;

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
@EnableWebSecurity
@Order(2)
@Slf4j
public class ConfigserverWebSecurityAdapter extends BasicWebSecurityAdapter {

    public static final String CLOUD_CLIENT = "CLOUD_CLIENT";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        baseHttpSecurity(http);
        http.authorizeRequests().mvcMatchers("/**").hasRole(CLOUD_CLIENT);
        http.authorizeRequests().anyRequest().authenticated();
    }



    @PostConstruct
    private void init() {
        UserDetails userDetails = User.withUsername(env.getProperty("spring.cloud.config.username"))
            .password(passwordEncoder.encode(env.getProperty("spring.cloud.config.password")))
            .roles(CLOUD_CLIENT)
            .build();
        userManager.createUser(userDetails);
    }
}






//            http.httpBasic().and().authorizeRequests()
//                .antMatchers("/**").permitAll().anyRequest().authenticated();


//http.csrf().disable();
//http.sessionManagement().disable();
//http.formLogin().disable();

//            http.authorizeRequests().antMatchers("/**").hasAnyRole()
//
//            http.authorizeRequests().antMatchers("/**").authenticated();
//http.authorizeRequests().antMatchers("/actuator/**").hasRole("CLOUD_CLIENT").and().httpBasic();


//basicHttpSecurity(http);
//http.antMatcher("/**").authorizeRequests().anyRequest().hasRole("CLOUD_CLIENT");



//            http
//                .csrf().disable()
//                .authorizeRequests().anyRequest().authenticated()
//                .and().httpBasic()
//                .and().sessionManagement().disable();

//            http.csrf().disable().sessionManagement().disable().formLogin().disable()
//                .authorizeRequests().antMatchers("/**").authenticated().and().httpBasic();

//http.formLogin().disable();

//            http
//                .csrf().disable()
//                .authorizeRequests().anyRequest().authenticated()
//                .and()
//                .httpBasic();

//            http
//                .httpBasic()
//                .and()
//                .authorizeRequests()
//                .antMatchers("/**").permitAll()
//                .anyRequest().authenticated();


//            http.csrf().disable();
//            http.sessionManagement().disable();
//            http.formLogin().disable();
//            http.httpBasic();

