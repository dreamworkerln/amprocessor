package ru.kvanttelecom.tv.amprocessor.core.amqp;

import com.pivovarit.function.ThrowingFunction;
import ru.kvanttelecom.tv.amprocessor.core.amqp.requests.AmqpRequest;
import ru.kvanttelecom.tv.amprocessor.core.amqp.responses.AmqpResponse;

public interface AmqpMethodHandler extends ThrowingFunction<AmqpRequest, AmqpResponse, Exception> {}