package ru.kvanttelecom.tv.amprocessor.configserver.configurations;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import ru.dreamworkerln.spring.utils.common.StringUtilsEx;

import java.util.Properties;


public class PreparedEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    /**
     * Will "replace" wrong property user.dir: 'file://' to 'file:/' when app was launched from / path
     * @param event ApplicationEnvironmentPreparedEvent
     */
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {

        //System.out.println("PreparedEventsListener called, context: " +  event.getBootstrapContext());

        String key = "spring.cloud.config.server.native.searchLocations";

        ConfigurableEnvironment env = event.getEnvironment();


        String value = env.getProperty(key);
        //System.out.println(env);
        if(!StringUtilsEx.isBlank(value)) {

            Properties props = new Properties();
            // fix user.dir = "/"
            if(value!= null && value.contains("file://")) {

                System.out.println("FIXING: " + key);
                value = value.replace("file://", "file:/");
                props.put(key, value);
                env.getPropertySources().addFirst(new PropertiesPropertySource(key, props));
            }
        }

    }
}
