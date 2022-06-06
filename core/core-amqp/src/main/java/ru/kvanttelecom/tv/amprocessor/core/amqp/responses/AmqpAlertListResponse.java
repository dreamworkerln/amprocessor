package ru.kvanttelecom.tv.amprocessor.core.amqp.responses;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AmqpAlertListResponse extends AmqpResponse {
    private final List<Alert> list;

    @JsonCreator
    public AmqpAlertListResponse(List<Alert> list) {
        this.list = list;
    }
}
