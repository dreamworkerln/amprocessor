package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base;

import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import lombok.extern.slf4j.Slf4j;
import ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.messages.BaseMessage;
import ru.kvanttelecom.tv.amprocessor.utils.Delegate;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * Topic channel, allows to subscribe, send request and handle response.
 * @param <T> message body
 */
@Slf4j
public class TopicChannel<T> {

    private final ITopic<BaseMessage<T>> topic;
    private UUID topicRegistration;
    private final Delegate<Message<BaseMessage<T>>> topicDelegate = new Delegate<>();

    public TopicChannel(ITopic<BaseMessage<T>> topic) {
        this.topic = topic;
    }

    public synchronized int addMessageHandler(Consumer<Message<BaseMessage<T>>> handler) {
        if(topicDelegate.size() == 0) {
            topicRegistration = topic.addMessageListener(topicDelegate::apply);
        }

        return topicDelegate.add(handler);
    }

    public synchronized void removeMessageHandler(int id) {

        if(!topicDelegate.containsKey(id)) {
            throw new IllegalArgumentException("Not registered: " + id);
        }

        topicDelegate.remove(id);
        if(topicDelegate.size() == 0) {
            topic.removeMessageListener(topicRegistration);
        }
    }

    /**
     * Send message
     * @param id message id, -1 for multicast (send to all subscribers)
     * @param body body
     */
    public void sendMessage(int id, T body) {
        BaseMessage<T> message = new BaseMessage<>(id, body);
        topic.publish(message);
    }


    /**
     * Emulation receiving message
     * @param id message id
     * @param body body
     */
    public void emulateMessageReceive(Message<BaseMessage<T>> message) {
        topicDelegate.apply(message);
    }
}
