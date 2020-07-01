package com.metrics.api.repository;

public class MetricAlreadyExistsException extends RuntimeException {

    public MetricAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
