package ru.kvanttelecom.tv.amprocessor.alerthandler.controllers.amqp.rpc.alert;

import org.springframework.beans.factory.annotation.Autowired;
import ru.kvanttelecom.tv.amprocessor.alerthandler.services.alert.AlertBroker;
import ru.kvanttelecom.tv.amprocessor.core.amqp.annotations.AmqpController;
import ru.kvanttelecom.tv.amprocessor.core.amqp.annotations.AmqpMethod;
import ru.kvanttelecom.tv.amprocessor.core.amqp.requests.AmqpGetAlertsFiring;
import ru.kvanttelecom.tv.amprocessor.core.amqp.responses.AmqpAlertListResponse;

@AmqpController
public class AmqpAlertController {

    @Autowired
    private AlertBroker alertBroker;


    @AmqpMethod
    public AmqpAlertListResponse getAlertsFiring(AmqpGetAlertsFiring request) {
        //List<Alert> alertsFiring = alertManager.getAlertsFiring();
        return new AmqpAlertListResponse(null);
    }

    




    /*
    @AmqpMethod
    public AmqpStreamListResponse findAllStreamByKey(AmqpFindAllStreamByKey request) {

        List<Stream> streams = streamMultiService.findAllByKey(request.getKeys());
        return new AmqpStreamListResponse(streamMapper.toDtoList(streams));
    }


    @AmqpMethod
    public AmqpStreamKeyListResponse findOfflineStreams(AmqpFindOfflineStream request) {

        List<StreamState> stats = streamStateMultiService.getOffline();
        List<StreamKey> keys = stats.stream().map(StreamState::getStreamKey).collect(Collectors.toList());
        return new AmqpStreamKeyListResponse(keys);
    }


    @AmqpMethod
    public AmqpStreamKeyListResponse findOfflineStreamsWithDuration(AmqpFindOfflineStreamWithDuration request) {
        List<StreamState> stats = streamStateMultiService.getOfflineWithDuration(request.getDuration());
        List<StreamKey> keys = stats.stream().map(StreamState::getStreamKey).collect(Collectors.toList());
        return new AmqpStreamKeyListResponse(keys);
    }

    @AmqpMethod
    public AmqpFindFlappingStreamKeyResponse findFlappingStreams(AmqpFindFlappingStream request) {
        Map<StreamKey,Double> periods = streamStateMultiService.getPeriods();
        return new AmqpFindFlappingStreamKeyResponse(periods);
    }
    */
}
