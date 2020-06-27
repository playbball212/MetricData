package com.metrics.api.repository;


import com.metrics.api.constants.ErrorCodes;
import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class CustomMetricRepository implements MetricRepository {


    private final ConcurrentHashMap<UUID, MetricItem> store = new ConcurrentHashMap<>();


    /**
     * API to save Metric Item Time Complexity -> O(metricItems.size()) / Space Complexity -> O(# OF Metrics ) + O(metricItems.size())
     *
     * @param metricItems - MetricItem Data Object
     * @return metricItem - Newly Saved Metric Item
     */
    @Override
    public List<MetricItem> save(List<MetricItemDTO> metricItems) {

        List<MetricItem> savedMetrics = new ArrayList<>();

        try {
            for (int i = 0; i < metricItems.size(); i++) {
                MetricItemDTO metricItemDTO = metricItems.get(i);
                List<Double> values = new ArrayList<>(Arrays.asList(Double.valueOf(metricItemDTO.getValue())));
                UUID metricId = UUID.randomUUID();
                MetricItem metricItem = new MetricItem(metricId, metricItemDTO.getName(), values);
                store.put(metricId, metricItem);
                savedMetrics.add(metricItem);
            }

        } catch (Exception e) {
            return savedMetrics;
        }

        return savedMetrics;

    }


    /**
     * API to Retrieve details about a metric ( Time Complexity O(1) Given Equal Distribution buckets / Space Complexity O(# OF METRICS)
     *
     * @param id - UUID of Metric
     * @return MetricItem -  containing values and name of metric
     */
    @Override
    public MetricItem find(String id) throws MetricDoestNotExistException {
        if (store.get(UUID.fromString(id)) != null) {
            return store.get(UUID.fromString(id));
        } else {
            throw new MetricDoestNotExistException(ErrorCodes.METRIC_DOES_NOT_EXIST);
        }
    }

    /**
     * API to retrieve Summary Statistics ( Time Complexity O( # OF DataPoints in Metrics) , Space Complexiity O(# OF METRICS)
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
            throw new MetricDoestNotExistException(ErrorCodes.METRIC_DOES_NOT_EXIST);
        }
    }

    /**
     * API to update metric with new value
     *
     * @param postedMetrics - posted metrics to be updated
     * @return MetricItem - Newly updated metric
     */
    @Override
    public List<MetricItem> update( List<UpdateItemDTO> postedMetrics) throws MetricDoestNotExistException {
        List<MetricItem> updatedMetrics = new ArrayList<>();
        for (int i = 0; i < postedMetrics.size(); i++) {
            UUID metricId = postedMetrics.get(i).getId();
            if (store.get(metricId) != null) {
                List<Double> values = store.get(metricId).getValues();
                values.add(Double.valueOf(postedMetrics.get(i).getValue()));
                updatedMetrics.add(new MetricItem(metricId, store.get(metricId).getName(), values));
            }
        }
        return updatedMetrics;
    }


    public void clear() {
        store.clear();
    }


}
