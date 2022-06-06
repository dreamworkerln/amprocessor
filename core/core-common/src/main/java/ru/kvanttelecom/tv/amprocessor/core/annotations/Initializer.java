package ru.kvanttelecom.tv.amprocessor.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * In you bean marked with @Initializer
 * define method 'void initialize()'
 * that will be called after all beans completely created and configured
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Initializer {
}
