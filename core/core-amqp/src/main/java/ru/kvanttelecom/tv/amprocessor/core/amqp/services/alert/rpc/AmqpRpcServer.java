package ru.kvanttelecom.tv.amprocessor.core.amqp.services.alert.rpc;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import ru.kvanttelecom.tv.amprocessor.core.amqp.AmqpIds;
import ru.kvanttelecom.tv.amprocessor.core.amqp.AmqpMethodHandler;
import ru.kvanttelecom.tv.amprocessor.core.amqp.requests.AmqpRequest;
import ru.kvanttelecom.tv.amprocessor.core.amqp.responses.AmqpErrorResponse;
import ru.kvanttelecom.tv.amprocessor.core.amqp.responses.AmqpResponse;
import ru.kvanttelecom.tv.amprocessor.core.amqp.annotations.AmqpController;
import ru.kvanttelecom.tv.amprocessor.core.amqp.annotations.AmqpMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;


/**
 * Dispatcher for @AmqpController and its @AmqpMethod
 * <br>
 * Use @Bean getAmqpRpcServer() to load AmqpRpcServer into context
 */
@Slf4j
//@AmqpDispatcher
public class AmqpRpcServer {

    private boolean haveBeenInitialized = false;

    // special mark for logging subsystem
    Marker marker = MarkerFactory.getMarker("AMQP_SERVER_MARKER");

    // <RequestClass, handler>
    private final Map<String, AmqpMethodHandler> handlers = new HashMap<>();

    private final ApplicationContext context;

    public AmqpRpcServer(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Amqp request dispatcher
     * <br>
     * One entrypoint for all amqp rpc requests
     * All rpc with different requests/responses go here
     */
    @RabbitListener(queues = AmqpIds.queue.alert.rpc.exec, id = "dispatcher", autoStartup = "false")
    @SneakyThrows
    private AmqpResponse exec(AmqpRequest request) {
        AmqpResponse result;

        try {

            log.debug(marker, "AMQP RPC request: {}", request);

            String methodName = request.getClass().getSimpleName();
            if (handlers.containsKey(methodName)) {
                result = handlers.get(methodName).apply(request);
            } else {

                result = new AmqpErrorResponse("Method " + methodName + " not found");
            }
            log.debug(marker, "AMQP RPC response: {}", result);
        }
        catch(Exception e) {
            log.error("Error: ", e);
            result = new AmqpErrorResponse(e);
        }
        return result;
    }


    // ================================================================================


    @EventListener
    public void handleContextRefresh(ApplicationStartedEvent event) {
        log.info("Application has been started");
//        if(haveBeenInitialized) {
//            return;
//        }
//        haveBeenInitialized = true;

        initHandlers();
    }

    // Auto call by @Initializer from InitializerBPP
//    private void initialize() {
//        initHandlers();
//    }

    /**
     * Add all @AmqpMethod from @AmqpController to handlers
     */
    private void initHandlers() {

        // only 1 time
        if(handlers.size() > 0) {
            return;
        }

        try {
            // Ask spring to find (and load if not loaded?) all beans annotated with @AmqpController
            Map<String,Object> beans = context.getBeansWithAnnotation(AmqpController.class);

            // https://stackoverflow.com/questions/27929965/find-method-level-custom-annotation-in-a-spring-context
            for (Map.Entry<String, Object> entry : beans.entrySet()) {

                Object bean = entry.getValue();
                Class<?> beanClass = bean.getClass();
                AmqpController controller = beanClass.getAnnotation(AmqpController.class);

                // bean is an AOP proxy
                if (controller == null) {
                    beanClass = AopProxyUtils.ultimateTargetClass(bean);
                    controller = beanClass.getAnnotation(AmqpController.class);
                }

                // Ищем в бине метод, помеченный аннотацией @AmqpMethod
                for (Method method : beanClass.getDeclaredMethods()) {

                    if (method.isAnnotationPresent(AmqpMethod.class)) {
                        //Should give you expected results
                        AmqpMethod amqpMethod = method.getAnnotation(AmqpMethod.class);

                        // Checked(как и unchecked) исключения будут проброшены к вызывающему
                        AmqpMethodHandler handler = request -> (AmqpResponse)method.invoke(bean, request);

                        // Get method name from method argument class type
                        String controllerMethodName = getArgClass(method).getSimpleName();

                        //String controllerMethodName = jrpcController.value() + "." + jrpcMethod.value();

                        // check that name is unique
                        if(handlers.containsKey(controllerMethodName)) {
                            throw new IllegalArgumentException("amqpController.amqpMethod: " +
                                controllerMethodName + " already exists (duplicate amqp method name)");
                        }
                        handlers.put(controllerMethodName, handler);

                    }
                }
            }

        }
        catch (BeansException e) {
            throw new RuntimeException(e);
        }

    }

    private Class<?> getArgClass(Method method) {

        Parameter[] parameters = method.getParameters();
        if(parameters.length != 1) {
            throw new IllegalStateException("AmqpMethod " + method.getClass().getSimpleName() + "." + method.getName() +
                "Wrong params count, should be one");
        }
        return parameters[0].getType();
    }


}




//
//
//            //log.debug("RPC REQUEST <FIND STREAMS> PARAMS: {}", request);
//
//            if (request instanceof AmqpFindOfflineStream) {
//
//                List<StreamState> stats = streamStateMultiService.getOffline();
//                List<StreamKey> keys = stats.stream().map(StreamState::getStreamKey).collect(Collectors.toList());
//                List<Stream> offline = streamMultiService.findAllByKey(keys);
//                result = new AmqpStreamFindOfflineResponse(streamMapper.toDtoList(offline));
//
//
//                //log.debug("RPC <FIND STREAMS> RESPONSE: {}", result);
//            }
//            else if(request instanceof AmqpFindStreamByKey) {
//
//                StreamKey key = ((AmqpFindStreamByKey) request).getKey();
//                List<Stream> list = List.of(streamMultiService.findByKey(key).get());
//                result = new AmqpStreamFindOneResponse(streamMapper.toDtoList(list));
//
//                //log.debug("RPC <FIND STREAMS> RESPONSE: {}", result);
//            }
//            else if(request instanceof AmqpFindFlappingStream) {
//
//                Map<StreamKey,Long> flapping = streamStateMultiService.getFlapCounts();
//                result = new AmqpFindFlappingStreamKeyResponse(flapping);
//                //log.debug("RPC <FIND STREAMS> RESPONSE: {}", result);
//            }
//        }
//        catch(Exception rethrow) {
//            throw new RuntimeException("StreamRpcServer.find error:", rethrow);
//        }
//        return result;





// @RabbitListener(queues = "#{@queueStreamRpcGetAll.getName()}")

/*


    @RabbitListener(queues = AmqpId.queue.stream.rpc.findAll)
    private List<Stream> findAll() {

        List<Stream> result;

        try {
            log.debug("RPC REQUEST <FIND STREAMS ALL>");
            result = streamService.findAll();
            log.debug("RPC <FIND STREAMS ALL> RESPONSE: {}", result);
        }
        catch(Exception rethrow) {
            log.error("StreamRpcServer.response error:", rethrow);
            throw rethrow;
        }
        return result;
    }



 */
