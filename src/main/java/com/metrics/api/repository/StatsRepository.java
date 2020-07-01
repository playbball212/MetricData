package com.metrics.api.repository;

import com.metrics.api.model.MetricItem;
import com.metrics.api.model.SummaryStatistics;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatsRepository {

    private final ConcurrentHashMap<UUID, SummaryStatistics> store = new ConcurrentHashMap<>();



}
