package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules;

import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.map.IMap;
import com.hazelcast.topic.Message;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.data.ModuleState;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.BaseCacheService;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.messages.BaseMessage;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.configurations.MessageStateChanged;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services.modules.configurations.ModulesCacheConfiguration;

import javax.annotation.PostConstruct;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Use MapStore / MapLoader
 * to init IMap / read write data from DB
 *
 * https://github.com/hazelcast/hazelcast-code-samples/blob/master/distributed-map/mapstore/src/main/java/LoadAll.java
 */


/**
 * This Map contains microservices statuses
 */
@Service
@Slf4j
public class ModulesService extends BaseCacheService<String, ModuleState, MessageStateChanged, Void> {

    //private final ConcurrentMap<UUID,String> memberModuleMap = new ConcurrentHashMap<>();

    private IMap<UUID, String> memberModuleMap;


    @Value("${spring.application.name}")
    @Getter
    protected String moduleName;

    @Value("#{${spring.application.dependencies}}")
    @Getter
    protected Set<String> dependencies;

    @Autowired
    public void setCacheConfiguration(ModulesCacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

    @PostConstruct
    protected void init() {

        ModulesCacheConfiguration modulesCacheConfiguration = (ModulesCacheConfiguration) cacheConfiguration;
        
        // get member UUID => module name Map
        memberModuleMap = instance.getMap(modulesCacheConfiguration.getMemberModuleMapName());

        // default listener - build memberModuleMap
        addRequestHandler(this::moduleStateChangeRequest);

        // Member DOWN handler
        addMemberDownHandler(this::memberDownHandler);

        // Mark current module as starting
        changeState(ModuleState.PENDING);
    }

    /**
     *  Get current module state
     */
    public ModuleState getState() {
        return get(moduleName);
    }


    /**
     * Set current module state to UP
     * <br>Do nothing if module already up
     */
    public void ready() {
        if(get(moduleName) != ModuleState.UP) {
            changeState(ModuleState.UP);
        }
    }


    /**
     * Check for this module if required dependencies are fulfilled
     */
    public boolean checkDependencies() {

        boolean result = true;

        for (String module : dependencies) {
            ModuleState state = get(module);
            if (state != ModuleState.UP) {
                result = false;
                break;
            }
        }
        return result;
    }

    // ======================================================================================



    private void memberDownHandler(MembershipEvent membershipEvent) {

        ModuleState DOWN = ModuleState.DOWN;
        UUID memberUUID = membershipEvent.getMember().getUuid();

        String memberName = memberModuleMap.get(memberUUID);
        Assert.notNull(memberName, "memberName == null (was evicted?)");

        //log.debug("memberDownHandler: module [{}] ({}) DOWN", memberName, memberUUID);
        //log.debug("memberModuleMap: {}", memberModuleMap);

        put(memberName, DOWN);

        // Create dummy TopicMessage
        MessageStateChanged stateMessage = new MessageStateChanged(memberName, DOWN);
        BaseMessage<MessageStateChanged> baseMessage = new BaseMessage<>(0, stateMessage);

        Message<BaseMessage<MessageStateChanged>> message = new Message<>(
            requestTopicName,
            baseMessage,
            Instant.now().toEpochMilli(),
            membershipEvent.getMember());

        // don't want to send message about member down to other members via sendRequest(...),
        // they will be notified about this same way (in memberDownHandler(...))
        emulateRequestReceive(message);
    }


    /**
     * Default module state changed handler
     * <br>Build map: &lt;member UUID,module name&gt;
     */
    private void moduleStateChangeRequest(Message<BaseMessage<MessageStateChanged>> message) {

        MessageStateChanged body = message.getMessageObject().body;
        UUID sender = message.getPublishingMember().getUuid();

        log.debug("Message: module [{}] now {}", body.moduleName, body.state);

        // append to memberModuleMap
        if(body.state != ModuleState.DOWN) {
            memberModuleMap.putIfAbsent(sender, body.moduleName);
        }
        // set TTL to entry, do not remove manually,
        // this will allow other members handle memberDownHandler/moduleStateChangeRequest
        else { // (1 min for debug purposes)
            memberModuleMap.setTtl(sender, 1, TimeUnit.MINUTES);
        }
    }



    /**
     * Change current module state
     * @param state state
     */
    private void changeState(ModuleState state) {
        //log.debug("Current module [{}] now: {}", moduleName, state);
        put(moduleName, state);
        sendRequest(nextId(), new MessageStateChanged(moduleName, state));
    }



    // not used
    private void logModuleStateChangeCaller(ModuleState state) {
        String callerClass = "Unknown";
        String callerMethod = "Unknown";
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        StackWalker.StackFrame callerFrame =
            walker.walk(frame -> frame.skip(2).findFirst()).orElse(null);

        if (callerFrame != null) {
            callerClass = callerFrame.getDeclaringClass().getSimpleName();
            callerMethod = callerFrame.getMethodName();
        }
        log.debug("{}.{} changed state: {}", callerClass, callerMethod, state);
    }




}





//    public void sendTopicFlushMessage(Set<String> list) {
//        topic.publish(list);
//    }




//    public void setInitLevel(int level) {
//
//        int oldLevel = (int)settings.get(CACHE_READY_LEVEL);
//        log.debug("Init level changing: {} -> {}", oldLevel, level);
//        settings.set(CACHE_READY_LEVEL, level);
//    }

//    public int getInitLevel() {
//        return (int) map.get(CACHE_INITLEVEL_LEVEL);
//    }

//    public void incrementReadyLevel() {
//        settings.set(CACHE_READY_LEVEL, settings.get(CACHE_READY_LEVEL));
//    }

//    public void incrementInitlevelIfEquals(int level) {
//        int oldLevel = (int)settings.get(CACHE_READY_LEVEL);
//        if(oldLevel == level) {
//            level++;
//            log.debug("Init level changing: {} -> {}", oldLevel, level);
//            settings.set(CACHE_READY_LEVEL, level);
//        }
//    }


//    /**
//     * Progress init level by 1.
//     * Do nothing if already progressed
//     */
//
//    public void initLevelNext() {
//
//        int oldLevel = (int) map.get(CACHE_INITLEVEL_LEVEL);
//        int newLevel = oldLevel + 1;
//        map.set(CACHE_INITLEVEL_LEVEL, newLevel);
//
//
//        String callerClass  = "Unknown";
//        String callerMethod = "Unknown";
//        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
//        StackWalker.StackFrame callerFrame =
//            walker.walk(frame -> frame.skip(1).findFirst()).orElse(null);
//
//        if(callerFrame != null) {
//            callerClass = callerFrame.getDeclaringClass().getSimpleName();
//            callerMethod = callerFrame.getMethodName();
//        }
//        log.debug("{}.{} changed InitLevel: {}", callerClass, callerMethod,  newLevel);
//
//        // send message about init level change
//        topic.publish(newLevel);
//    }




//    public boolean isReady() {
//        Object tmp = settings.get(CACHE_READY);
//        return tmp != null && (boolean) tmp;
//    }



//    public void setReady() {
//        instance.getMap(MAP_SETTINGS).set(CACHE_READY, true);
//    }


//    /**
//     * Add Module State changed handler
//     * @param handler handler
//     * @return registration id
//     */
//    public synchronized UUID addModuleStateChangeHandler(Consumer<Pair<String,ModuleState>> handler) {
//
////        topic.addMessageListener(message -> {
////            Pair<String,ModuleState> change = message.getMessageObject();
////            log.debug("Event - module [{}] state changed: {}", change.getLeft(), change.getRight());
////            moduleDelegate.sendAll(change);
//        });


//        return moduleDelegate.add(handler);
//    }

//    /**
//     * Remove Module State changed handler by registration id
//     * @param id registration id from addModuleStateChangeHandler() result
//     */
//    public synchronized void removeModuleStateChangeHandler(UUID id) {
//        moduleDelegate.remove(id);
//    }


//@Value("${spring.boot.initlevel.required:#{T(Integer).MAX_VALUE}}")



//private FencedLock lock;
//lock = instance.getCPSubsystem().getLock("ModulesService.memberDown.lock"