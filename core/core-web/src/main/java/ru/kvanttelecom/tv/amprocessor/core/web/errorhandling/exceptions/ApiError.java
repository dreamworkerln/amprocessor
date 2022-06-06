package ru.kvanttelecom.tv.amprocessor.core.web.errorhandling.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@Data
public class ApiError {

    private HttpStatus status;
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-yyyy-MM-dd hh:mm:ss UTC")
    private Instant timestamp;
    private String message;
    private String debugMessage;
    private List<ApiSubError> subErrors;

    public ApiError() {
        timestamp = Instant.now();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }
}