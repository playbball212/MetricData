package com.metrics.api.repository;

public class MetricDoestNotExistException extends Exception {

    public MetricDoestNotExistException(String message) {
        super(message);
    }

}
