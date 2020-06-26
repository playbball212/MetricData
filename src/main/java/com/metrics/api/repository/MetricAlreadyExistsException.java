package com.metrics.api.repository;

public class MetricAlreadyExistsException extends Exception {

    public MetricAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
