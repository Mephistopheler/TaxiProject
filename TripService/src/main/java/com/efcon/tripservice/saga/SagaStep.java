package com.efcon.tripservice.saga;

@FunctionalInterface
public interface SagaStep {

    void execute();

    default void compensate() {

    }
}