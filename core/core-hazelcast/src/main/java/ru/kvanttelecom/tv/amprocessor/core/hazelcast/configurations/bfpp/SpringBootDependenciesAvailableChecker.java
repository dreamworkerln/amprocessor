package ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.bfpp;

import com.google.common.util.concurrent.Monitor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.ModulesService;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * ....
 * Other beans like Spring TaskSchedulerBuilder, DispatcherServlet will be altered to depend on this SpringBootReadinessChecker
 * So Spring Boot application execution will be suspended until custom hazelcast cache will be initialized
 */
@Slf4j
public class SpringBootDependenciesAvailableChecker {


    public static final Integer REQUIRED_MAX_WAIT_SECONDS = 240;


    @Autowired
    ModulesService modulesService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Environment environment;

    @Getter
    @Value("${spring.application.name}")
    private String moduleName;

    @Getter
    @Value("#{${spring.application.dependencies}}")
    private Set<String> dependencies;


    private final Semaphore semaphore = new Semaphore(0);

//    private Monitor monitor = new Monitor();
//    private final Monitor.Guard responsePresent = new Monitor.Guard(monitor) {
//        @Override
//        public boolean isSatisfied() {
//            return false;
//        }
//    };


    @PostConstruct
    private void init() {

        // Disable waiting on tests
        // ------------------------------------------------------------------
        if(Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            log.error("Test environment, no wait performed.");
        }

        log.debug("Starting module: [{}], dependencies: {}", moduleName, dependencies);
        log.debug("Waiting for dependencies modules ...");



        // registering for TOPIC_MODULES_STATE_CHANGED events
        int regId = modulesService.addRequestHandler(response -> {
            if (modulesService.checkDependencies()) {
                // tell that all dependencies was met
                semaphore.release();
            }
        });

        try {

            Instant start = Instant.now();
            while (!modulesService.checkDependencies()) {

                // waiting to response 1 sec
                boolean responded = semaphore.tryAcquire(1000, TimeUnit.MILLISECONDS);

                // If got signaled
                if(responded) {
                    continue;
                }

//                // If got signaled and all dependencies are UP
//                if(responded && modulesService.checkDependencies()) {
//                    break;
//                }

                Duration d = Duration.between(start, Instant.now());
                log.debug("{}", d.toSeconds());

                // check timeout - if true => terminate spring boot application
                if (d.toSeconds() >= REQUIRED_MAX_WAIT_SECONDS) {
                    log.error("Waiting timeout after: {} seconds, " + "terminating application.", d.toSeconds());
                    closeContext();
                    return;
                }
            }

        } catch (InterruptedException rethrow) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Things are bad", rethrow);
        }
        finally {
            modulesService.removeRequestHandler(regId);
        }

        log.debug("All dependencies are available, continue booting.");
    }

    // Halt spring boot app
    private void closeContext() {
        ((ConfigurableApplicationContext)context).close();
    }

}






//public static final String READY_LEVEL_REQUIRED_KEY = "REQUIRED_READY_LEVEL_KEY";
//    Properties props = System.getProperties();
//    int requiredLevel = Integer.parseInt(props.getProperty(READY_LEVEL_REQUIRED_KEY, String.valueOf(Integer.MAX_VALUE)));
//    Properties props = System.getProperties();
//    props.setProperty(READY_LEVEL_REQUIRED_KEY, String.valueOf(level));

//        if(System.getProperty("spring.profiles.active", "default").equals("test")) {
//            return;
//            }

