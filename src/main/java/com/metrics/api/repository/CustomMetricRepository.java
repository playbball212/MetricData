package com.metrics.api.repository;


import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.model.MetricItem;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CustomMetricRepository implements MetricRepository {

    private final ConcurrentHashMap<UUID, MetricItem> store = new ConcurrentHashMap<>();


    /**
     * API to save Metric Item
     *
     * @param metric - MetricItem Data Object
     * @return metricItem - Newly Saved Metric Item
     */
    @Override
    public MetricItem save(MetricItemDTO metric) throws MetricAlreadyExistsException {

        UUID uuid = UUID.randomUUID();
        List<Double> values = new ArrayList<>(Arrays.asList(Double.valueOf(metric.getValue())));
        MetricItem metricItem = new MetricItem(uuid, metric.getName(), values);

        // Check if name already exists
        if (store.values().contains(metricItem)) {
            throw new MetricAlreadyExistsException("Metric Already Exists");
        }

        store.put(uuid, metricItem);
        return metricItem;


    }


    /**
     * API to Retrieve details about a metric
     *
     * @param id - UUID of Metric
     * @return MetricItem -  containing values and name of metric
     */
    @Override
    public MetricItem find(String id) throws MetricDoestNotExistException {
        if (store.get(UUID.fromString(id)) != null) {
            return store.get(UUID.fromString(id));
        } else {
            throw new MetricDoestNotExistException();
        }
    }

    /**
     * API to retrieve Summary Statistics
     *
     * @param id UUID of Metric
     * @return Summary Statistics of Metric including mean , median , minimum value , and maximum value
     * @throws MetricDoestNotExistException
     */
    @Override
    public DoubleSummaryStatistics findStatsForMetric(String id) throws MetricDoestNotExistException {
        MetricItem item;
        if (store.get(UUID.fromString(id)) != null) {
            item = store.get(UUID.fromString(id));
            List<Double> metricDataPoints = item.getValues();
            return metricDataPoints.stream().mapToDouble(d -> d).summaryStatistics();


        } else {
            throw new MetricDoestNotExistException();
        }
    }

    /**
     * API to update metric with new value
     *
     * @param id            - UUID of Metric
     * @param metricItemDTO - MetricItemDTO containing new values
     * @return MetricItem - Newly updated metric
     */
    @Override
    public MetricItem update(String id, MetricItemDTO metricItemDTO) throws MetricDoestNotExistException {
        MetricItem item;
        if (store.get(UUID.fromString(id)) != null) {
            item = store.get(UUID.fromString(id));
            Double metricData = Double.valueOf(metricItemDTO.getValue());
            List<Double> values = item.getValues();
            values.add(metricData);
        } else {
            throw new MetricDoestNotExistException();
        }
        return item;
    }


}
