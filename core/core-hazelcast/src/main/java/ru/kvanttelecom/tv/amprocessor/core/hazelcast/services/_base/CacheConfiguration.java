package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.ReliableTopicConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;


/**
 *  CacheConfiguration beans used to append Hazelcast Config
 *  <br>before creating HazelcastInstance
 */
@Slf4j
public abstract class CacheConfiguration {

    @Getter
    protected String mapName;
    @Getter
    protected String requestTopicName;
    @Getter
    protected String responseTopicName;

    @Autowired
    protected Config config;



    @PostConstruct
    private void init() {

        setNames();
        //
        config.addMapConfig(configureMap());
        config.addReliableTopicConfig(configureTopicRequest());
        config.addReliableTopicConfig(configureTopicResponse());

        // custom configuration
        addAdditionalConfiguration(config);
    }

    /**
     * In descendants define mapName, requestTopicName, responseTopicName values in this method
     */
    protected abstract void setNames();

    /**
     * Default Imap configuration
     */
    public MapConfig configureMap() {
        MapConfig result = new MapConfig(mapName);
        //result.setTimeToLiveSeconds(360);
        //result.setMaxIdleSeconds(20);
        return result;
    }


    /**
     * Default reliableTopic request configuration
     */
    public ReliableTopicConfig configureTopicRequest() {
        ReliableTopicConfig result = new ReliableTopicConfig()
            .setStatisticsEnabled(false)
            .setName(requestTopicName);
        return result;
    }

    /**
     * Default reliableTopic request configuration
     */
    public ReliableTopicConfig configureTopicResponse() {
        ReliableTopicConfig result = new ReliableTopicConfig()
            .setStatisticsEnabled(false)
            .setName(responseTopicName);
        return result;
    }

    /**
     * Configure here additional hazelcast objects if you need this
     * @param config hazelcast Config
     */
    public void addAdditionalConfiguration(Config config) {}


    // =================================================================

//
//    private void checkHazelcastObjects() {
//
//        if(isBlank(mapName)) {
//            throw new IllegalStateException("mapName not configured");
//        }
//        if(isBlank(requestTopicName)) {
//            throw new IllegalStateException("requestTopicName not configured");
//        }
//        if(isBlank(responseTopicName)) {
//            throw new IllegalStateException("responseTopicName not configured");
//        }
//    }

}
