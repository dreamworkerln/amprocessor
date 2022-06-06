package ru.kvanttelecom.tv.amprocessor.utils.functions;

@FunctionalInterface
public interface TriFunction<A,B,C,R> {
    R apply(A a, B b, C c);
}
