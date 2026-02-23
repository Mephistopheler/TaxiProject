package com.efcon.tripservice.saga;

public class SagaExecutionException extends RuntimeException {

    public SagaExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}