package ru.kvanttelecom.tv.amprocessor.core.amqp.responses;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;


@Data
public class AmqpErrorResponse extends AmqpResponse {
    private Exception e;

    @JsonCreator
    public AmqpErrorResponse(String message) {
        e = new RuntimeException(message);
    }

    @JsonCreator
    public AmqpErrorResponse(Exception e) {
        this.e = e;
    }
}
