package ru.kvanttelecom.tv.amprocessor.core.hazelcast.services._base.messages;

import java.io.Serializable;

///@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property = "@type")

/**
 * Container for messages
 * @param <T>
 */
public class BaseMessage<T> implements Serializable {

    public final Integer id;
    //@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property = "@type")
    public final T body;

    public BaseMessage(int id, T body) {
        this.id = id;
        this.body = body;
    }

    @Override
    public String toString() {
        return "BaseMessage{" +
            "id=" + id +
            ", body=" + body +
            '}';
    }
}
