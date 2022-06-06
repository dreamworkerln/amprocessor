package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base;

import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.topic.Message;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.data.SemaphoreResponsePair;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.messages.BaseMessage;
import ru.kvanttelecom.tv.amprocessor.utils.Delegate;
import ru.kvanttelecom.tv.amprocessor.utils.exceptions.TimeoutExceptionR;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;

import static ru.dreamworkerln.spring.utils.common.StringUtilsEx.formatMsg;

@Slf4j
// REQ - request body
// RSP - response body
public abstract class BaseCacheService<K, V, REQ, RSP> {

    private static final int REQUEST_TIMEOUT_SECONDS = 240;

    //private final static LongUnaryOperator longCycleIncrementator = (i) -> i == Long.MAX_VALUE ? 0 : i + 1;
    private final static IntUnaryOperator intCycleIncrementator = (i) -> i == Integer.MAX_VALUE ? 0 : i + 1;
    private final AtomicInteger idGen = new AtomicInteger(1);

    protected String mapName;
    protected String requestTopicName;
    protected String responseTopicName;


    private IMap<K, V> map;
    private TopicChannel<REQ> requestTopic;
    private TopicChannel<RSP> responseTopic;
    private final Delegate<MembershipEvent> memberRemovedDelegate = new Delegate<>();

    // response handlers map
    private final ConcurrentMap<Integer, SemaphoreResponsePair<RSP>> syncRequests = new ConcurrentHashMap<>();

    protected CacheConfiguration cacheConfiguration;

    protected int defaultResponseListenerRegistration;

    // ----------------------------------------------


    @Autowired
    protected HazelcastInstance instance;


    @PostConstruct
    private void init() {

        log.debug(this.getClass().getSimpleName() + ": BaseCacheService init()");

        if(cacheConfiguration == null) {
            String msg = formatMsg("{}: CacheConfiguration not configured", this.getClass().getSimpleName());
            throw new NotImplementedException(msg);
        }

        mapName = cacheConfiguration.getMapName();
        requestTopicName = cacheConfiguration.getRequestTopicName();
        responseTopicName = cacheConfiguration.getResponseTopicName();

        // gat hazelcast structures
        map = instance.getMap(mapName);
        requestTopic = new TopicChannel<>(instance.getReliableTopic(requestTopicName));
        responseTopic = new TopicChannel<>(instance.getReliableTopic(responseTopicName));

        // add default response listener for synchronous requests ---------------------
        defaultResponseListenerRegistration = responseTopic.addMessageHandler(this::defaultResponseListener);

        // apply hazelcast Member removed delegate  -----------------------------------
        instance.getCluster().addMembershipListener(new MemberUpDownListener());


    }

    public V get(K key) {
        return map.get(key);
    }

    public V put(K key, V value) {
        map.put(key, value);
        return value;
    }

    public void delete(K key) {
        map.remove(key);
    }


    public List<V> getList() {
        return new ArrayList<>(map.values());
    }

    public ConcurrentMap<K, V> getMap() {
        return map;
    }

    public long count() {
        return map.size();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public void putAll(Map<K,V> map) {
        map.entrySet().addAll(map.entrySet());
    }

    public void clear() {
        map.clear();
    }

    public TransactionContext getTransactionContext() {
        TransactionOptions options =
            new TransactionOptions().setTransactionType(TransactionOptions.TransactionType.TWO_PHASE);
        return instance.newTransactionContext(options);
    }


    // Subscriptions -----------------------------------------------------


    public int addRequestHandler(Consumer<Message<BaseMessage<REQ>>> handler) {
        return requestTopic.addMessageHandler(handler);
    }

    public synchronized void removeRequestHandler(int regId) {
        requestTopic.removeMessageHandler(regId);
    }

    public int addResponseHandler(Consumer<Message<BaseMessage<RSP>>> handler) {
        return responseTopic.addMessageHandler(handler);
    }
    public synchronized void removeResponseHandler(int regId) {
        responseTopic.removeMessageHandler(regId);
    }

    // --------------------------

    /**
     * Add handler about hazelcast member down
     * @param handler Consumer &lt;member UUID&gt;
     * @return registration id
     */
    public int addMemberDownHandler(Consumer<MembershipEvent> handler) {
        return memberRemovedDelegate.add(handler);
    }

    public void removeMemberDownHandler(int regId) {
        memberRemovedDelegate.remove(regId);
    }


    /**
     * Send request
     * @param id always obtain id to submit here by calling nextId()
     * @param body request.body
     */
    public void sendRequest(int id, REQ body) {
        requestTopic.sendMessage(id, body);
    }

    public RSP sendRequestSync(REQ request) {

        RSP result = null;

        int id = nextId();
        syncRequests.put(id, new SemaphoreResponsePair<>());
        requestTopic.sendMessage(id, request);

        try {

            Instant start = Instant.now();
            while(!syncRequests.get(id).isResponded()) {

                // waiting to response
                boolean responded = syncRequests.get(id).getSemaphore().tryAcquire(1000, TimeUnit.MILLISECONDS);

                // exit in middle
                if(responded) {
                    continue;
                }

                // check timeout
                Duration d = Duration.between(start, Instant.now());
                if (d.toSeconds() >= REQUEST_TIMEOUT_SECONDS) {
                    log.error("Waiting timeout after: {} seconds, " + " aborting", d.toSeconds());
                    throw new TimeoutExceptionR("Timeout after " + d.toSeconds() + " seconds");
                }
            }
            // get result
            result = syncRequests.get(id).getResponse();

        }
        catch (InterruptedException rethrow) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Things are bad, interrupted while waiting response", rethrow);
        }
        finally {
            // clear request
            syncRequests.remove(id);
        }
        return result;
    }

    /**
     * Reply on response
     * @param id request.id
     * @param body response body
     */
    public void reply(int id, RSP body) {
        responseTopic.sendMessage(id, body);
    }


    /**
     * Get next generated message id
     * @return id
     */
    public int nextId() {
        return idGen.getAndUpdate(intCycleIncrementator);
    }


    /**
     * Emulation receiving message
     * @param message hazelcast Message
     */
    public void emulateRequestReceive(Message<BaseMessage<REQ>> message) {
        requestTopic.emulateMessageReceive(message);
    }


    // =========================================================================




    private void defaultResponseListener(Message<BaseMessage<RSP>> message) {
        BaseMessage<RSP> response = message.getMessageObject();

        int id = response.id;
        if(syncRequests.containsKey(id)) {
            SemaphoreResponsePair<RSP> pair = syncRequests.get(id);
            Semaphore semaphore = pair.getSemaphore();
            pair.setResponse(response.body);
            // tell that response is ready
            semaphore.release();
        }
    }


    private class MemberUpDownListener implements MembershipListener {

        @Override
        public void memberAdded(MembershipEvent membershipEvent) {

        }

        @Override
        public void memberRemoved(MembershipEvent membershipEvent) {
            memberRemovedDelegate.apply(membershipEvent);
        }
    }



    @PreDestroy
    private void destroy() {
        log.info("Shutdown hazelcast instance");
        instance.shutdown();
    }

}







//    public RSP sendRequestSync(REQ requestBody) {
//
//        Object lock = new Object();
//
//        var request = new Object() {UUID id;  REQ body = requestBody;};
//        var response = new Object() {UUID id; RSP body;};
//
//        UUID regId = responseTopic.addTopicDelegate(message -> {
//            if (message.id.equals(request.id)) {
//                response.body = message.body;
//                synchronized (lock) {
//                    lock.notifyAll();
//                }
//            }
//        });
//
//        try {
//            request.id = requestTopic.sendMessage(request.body);
//            synchronized (lock) {
//                lock.wait();
//            }
//        }
//        catch (InterruptedException e){
//            Thread.currentThread().interrupt();
//        }
//        finally {
//            responseTopic.removeTopicDelegate(regId);
//        }
//        return response.body;
//    }









// ==========================================================


//    public void deleteList(Iterable<Camera> list) {
//        list.forEach(camera -> map.remove(camera.getName()));
//    }
