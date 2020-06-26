package com.metrics.api.repository;


import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.model.MetricItem;

import java.util.DoubleSummaryStatistics;


public interface MetricRepository {

    MetricItem save(MetricItemDTO metric) throws MetricAlreadyExistsException;

    MetricItem find(String id) throws MetricDoestNotExistException;

    DoubleSummaryStatistics findStatsForMetric(String id) throws MetricDoestNotExistException;

    MetricItem update(String id , MetricItemDTO metricItemDTO) throws MetricDoestNotExistException;
}
