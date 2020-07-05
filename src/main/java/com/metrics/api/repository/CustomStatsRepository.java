package com.metrics.api.repository;

import com.metrics.api.model.MetricItem;
import com.metrics.api.model.SummaryStatistics;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CustomStatsRepository implements StatsRepository {

    private final ConcurrentHashMap<UUID, SummaryStatistics> store = new ConcurrentHashMap<>();

    @Async
    @Override
    public void calculateStatsForMetrics(List<MetricItem> savedMetrics) {

        for (int i = 0; i < savedMetrics.size(); i++) {
            MetricItem metricItem = savedMetrics.get(i);
            if (metricItem.getValues().size() > 1) {
                // Do Update
                updateStats(metricItem);

            } else {
                initializeStats(metricItem);
            }
        }
    }


    /**
     * API to retrieve Summary Statistics
     *
     * @param uuids List of UUIds to view Summaries
     * @return Summary Statistics of Metric including mean , median , minimum value , and maximum value
     * @throws MetricDoestNotExistException
     */
    @Override
    public List<SummaryStatistics> findStatsForMetric(List<UUID> uuids) throws MetricDoestNotExistException {
        List<SummaryStatistics> summaryStatistics = new ArrayList<>();
        for (int i = 0; i < uuids.size(); i++) {
            UUID uuid = uuids.get(i);
            if (store.get(uuid) == null) {
                throw new MetricDoestNotExistException("Metric does not exist");
            }

            SummaryStatistics stat = store.get(uuid);
            summaryStatistics.add(stat);
        }

        return summaryStatistics;
    }

    private void updateStats(MetricItem item) {
        SummaryStatistics previousStats = store.get(item.getId());

        int count = item.getValues().size();
        double insertedItem = item.getValues().get(count - 1);
        // Calculate Average
        double sum = item.getValues().stream().mapToDouble(d -> d).sum();

        double average = sum / count;
        previousStats.setMean(average);

        // Calculate Min
        if (insertedItem < previousStats.getMin()) {
            previousStats.setMin(insertedItem);
        }

        // Calculate Max
        if (insertedItem > previousStats.getMax()) {
            previousStats.setMax(insertedItem);
        }
    }

    private void initializeStats(MetricItem item) {
        Double initialMetric = item.getValues().get(0);
        SummaryStatistics summaryStatistics = new SummaryStatistics(initialMetric, initialMetric, initialMetric,
                initialMetric, item.getId().toString());
        store.put(item.getId(), summaryStatistics);
    }
}
