package ru.kvanttelecom.tv.amprocessor.utils.exceptions;

public class TimeoutExceptionR extends RuntimeException {


    public TimeoutExceptionR(String message) {
        super(message);
    }

    public TimeoutExceptionR(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutExceptionR(Throwable cause) {
        super(cause);
    }


}
