package com.metrics.api.repository;


import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;

import java.util.DoubleSummaryStatistics;
import java.util.List;


public interface MetricRepository {

    List<MetricItem> save(List<MetricItemDTO> metric) throws MetricAlreadyExistsException;

    MetricItem find(String id) throws MetricDoestNotExistException;

    DoubleSummaryStatistics findStatsForMetric(String id) throws MetricDoestNotExistException;

    List<MetricItem> update( List<UpdateItemDTO> metricItems) throws MetricDoestNotExistException;
}
