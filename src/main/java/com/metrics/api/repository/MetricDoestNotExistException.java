package com.metrics.api.repository;

public class MetricDoestNotExistException extends RuntimeException {

    public MetricDoestNotExistException(String message) {
        super(message);
    }

}
