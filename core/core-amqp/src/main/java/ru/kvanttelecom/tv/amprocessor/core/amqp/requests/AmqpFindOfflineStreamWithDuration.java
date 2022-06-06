package ru.kvanttelecom.tv.amprocessor.core.amqp.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.time.Duration;

@Data
public class AmqpFindOfflineStreamWithDuration extends AmqpRequest {

    private final Duration duration;

    @JsonCreator
    public AmqpFindOfflineStreamWithDuration(Duration duration) {
        this.duration = duration;
    }
}
