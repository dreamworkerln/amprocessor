package ru.kvanttelecom.tv.amprocessor.core.amqp.annotations;

import ru.kvanttelecom.tv.amprocessor.core.annotations.Initializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark bean as AMQP rpc dispatcher
 * and will build dispatcher.handlers at app startup
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Initializer
public @interface AmqpDispatcher {
}
