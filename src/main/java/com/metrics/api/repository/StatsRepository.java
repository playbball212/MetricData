package com.metrics.api.repository;

import com.metrics.api.model.MetricItem;
import com.metrics.api.model.SummaryStatistics;

import java.util.List;
import java.util.UUID;

public interface StatsRepository {

     void calculateStatsForMetrics(List<MetricItem> savedMetrics);

     List<SummaryStatistics> findStatsForMetric(List<UUID> metricSummary) throws MetricDoestNotExistException;

}
