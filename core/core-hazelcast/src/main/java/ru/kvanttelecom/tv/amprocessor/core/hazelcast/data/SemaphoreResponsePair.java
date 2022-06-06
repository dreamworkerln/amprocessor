package ru.kvanttelecom.tv.amprocessor.core.hazelcast.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.concurrent.Semaphore;

@Data
@AllArgsConstructor
public class SemaphoreResponsePair<RSP> {

    /**
     * Response has arrived
     */
    @Getter
    private boolean responded = false;
    // Semaphore(0) mean that requesting thread always will wait response
    private final Semaphore semaphore = new Semaphore(0);

    /**
     * Response itself (may contain null)
     */
    private RSP response = null;

    public void setResponse(RSP response) {
        this.response = response;
        this.responded = true;
    }

    public SemaphoreResponsePair() {}
}
