package ru.kvanttelecom.tv.amprocessor.core.amqp.services.alert.rpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.kvanttelecom.tv.amprocessor.core.amqp.AmqpIds;
import ru.kvanttelecom.tv.amprocessor.core.amqp.requests.AmqpGetAlertsFiring;
import ru.kvanttelecom.tv.amprocessor.core.amqp.responses.AmqpAlertListResponse;
import ru.kvanttelecom.tv.amprocessor.core.data.alert.Alert;
import java.util.*;
import ru.kvanttelecom.tv.amprocessor.core.amqp.requests.AmqpRequest;

@Slf4j
public class AmqpRpcClient {

    @Service
    public static class Alerts {

        private static final String exchanger = AmqpIds.exchanger.alert.rpc.exec;
        private static final String routing = AmqpIds.binding.alert.rpc.exec;


        @Autowired
        private RabbitTemplate template;


        public List<Alert> getFiring() {

            List<Alert> result;
            log.debug("RPC REQUEST <GET FIRING ALERTS>");
            AmqpRequest request = new AmqpGetAlertsFiring();
            ParameterizedTypeReference<AmqpAlertListResponse> responseTypeRef = new ParameterizedTypeReference<>() {
            };
            var response = template.convertSendAndReceiveAsType(exchanger, routing, request, responseTypeRef);
            if (response == null) {
                throw new RuntimeException("RPC <GET FIRING ALERTS>: NO RESPONSE");
            }
            result = response.getList();

            log.debug("RPC RESPONSE: {}", result);
            return result;
        }
    }
}

