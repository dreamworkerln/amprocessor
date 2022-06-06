package ru.kvanttelecom.tv.amprocessor.core.amqp.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kvanttelecom.tv.amprocessor.core.amqp.AmqpIds;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class AMQPConfiguration {

    // NEW ALERTS
    private FanoutExchange exchangeAlertsNew;
    private Queue queueAlertsNew;
    private Binding bindingAlertsNew;

    //  RPC GET FIRING ALERTS
    private Queue queueAlertRpcExec;
    private DirectExchange exchangeAlertRpcExec;
    private Binding bindingAlertRpcExec;


    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public MessageConverter jackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }





    @PostConstruct
    private void postConstruct() {

        // Alerts new

        exchangeAlertsNew = new FanoutExchange(AmqpIds.exchanger.alert.msg.new_alert);
        //queueAlertsNew = new Queue(AmqpIds.queue.alert.msg.new_alert);
        queueAlertsNew = new AnonymousQueue();

        // seems like using empty routing key to broadcast
        bindingAlertsNew = BindingBuilder.bind(queueAlertsNew)
            .to(exchangeAlertsNew);


        // RPC - One entry point, then dispatching -------------------------

        queueAlertRpcExec = new Queue(AmqpIds.queue.alert.rpc.exec);
        exchangeAlertRpcExec = new DirectExchange(AmqpIds.exchanger.alert.rpc.exec);
        bindingAlertRpcExec = BindingBuilder.bind(queueAlertRpcExec)
            .to(exchangeAlertRpcExec).with(AmqpIds.binding.alert.rpc.exec);

    }


    // Alerts new

    @Bean(AmqpIds.exchanger.alert.msg.new_alert)
    public FanoutExchange exchangeAlertsNew() {
        return exchangeAlertsNew;
    }

    @Bean/*(AmqpIds.queue.alert.msg.new_alert)*/
    public Queue queueAlertsNew() {
        return queueAlertsNew;
    }


    // Bindings
    @Bean/*(AmqpIds.binding.alert.msg.new_alert)*/
    public Binding bindingAlertsNew() {
        return bindingAlertsNew;
    }

    // RPC

    @Bean(AmqpIds.queue.alert.rpc.exec)
    public Queue queueStreamRpcFind() {
        return queueAlertRpcExec;
    }

    @Bean(AmqpIds.exchanger.alert.rpc.exec)
    public DirectExchange exchangeStreamRpcFind() {
        return exchangeAlertRpcExec;
    }

    @Bean(AmqpIds.binding.alert.rpc.exec)
    public Binding bindingStreamRpcFind() {
        return bindingAlertRpcExec;
    }

//
//    @Bean(AmqpId.queue.stream.events.update)
//    public Queue queueStreamEvent() {
//        return queueStreamEventUpdate;
//    }
//
//    @Bean(AmqpId.exchanger.stream.events.update)
//    public DirectExchange exchangeStreamRpcEvent() {
//        return exchangeStreamEventUpdate;
//    }
//
//    @Bean(AmqpId.binding.stream.events.update)
//    public Binding bindingStreamEvent() {
//        return bindingStreamEventUpdate;
//    }
//
//
    // RPC ---------------------------------


//
//
//    // FindByKeys
//    @Bean(AmqpId.queue.stream.rpc.findByKeys)
//    public Queue queueStreamRpcFindByKeys() {
//        return queueStreamRpcFindByKeys;
//    }
//
//    @Bean(AmqpId.exchanger.stream.rpc.findByKeys)
//    public DirectExchange exchangeStreamRpcFindByKeys() {
//        return exchangeStreamRpcFindByKeys;
//    }
//
//    @Bean(AmqpId.binding.stream.rpc.findByKeys)
//    public Binding bindingStreamRpcFindByKeys() {
//        return bindingStreamRpcFindByKeys;
//    }

    // -------------------------------------------------------------------------------------
}
