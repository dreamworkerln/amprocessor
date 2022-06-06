package ru.kvanttelecom.tv.amprocessor.core.hazelcast.configurations.cache;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface HazelcastConfiguration {
}
