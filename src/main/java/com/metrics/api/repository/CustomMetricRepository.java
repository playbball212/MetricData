package com.metrics.api.repository;


import com.metrics.api.constants.ErrorCodes;
import com.metrics.api.datatransferobjects.SaveItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.model.SummaryStatistics;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Service
public class CustomMetricRepository implements MetricRepository {


    private final ConcurrentHashMap<UUID, MetricItem> store = new ConcurrentHashMap<>();

    /**
     * API to save Metric Item
     *
     * @param postedMetrics - MetricItem Data Object
     * @return metricItem - Newly Saved Metric Item
     */
    @Override
    public List<MetricItem> save(List<SaveItemDTO> postedMetrics) {

        List<MetricItem> savedMetrics = new ArrayList<>();


        for (int i = 0; i < postedMetrics.size(); i++) {
            SaveItemDTO saveItemDTO = postedMetrics.get(i);
            List<Double> values = new ArrayList<>(Arrays.asList(Double.valueOf(saveItemDTO.getValue())));
            UUID metricId = UUID.randomUUID();
            MetricItem metricItem = new MetricItem(metricId, saveItemDTO.getName(), values);
            store.put(metricId, metricItem);
            savedMetrics.add(metricItem);
        }


        return savedMetrics;

    }


    /**
     * HELPER API FOR TESTING
     *
     * @param id - UUID of Metric
     * @return MetricItem -  containing values and name of metric
     */
    @Override
    public MetricItem  find(String id) throws MetricDoestNotExistException {
        try {
            MetricItem item = store.get(UUID.fromString(id));
            if (item != null) {
                return item;
            } else {
                throw new MetricDoestNotExistException("Metric does not exist");
            }
        } catch (IllegalArgumentException | MetricDoestNotExistException e) {
                throw e;
        }
    }



    /**
     * Helper Method to retrieve Median depending on size of values
     *
     * @param sortedDouble
     * @return Median Value
     */
    private Double getMedian(List<Double> sortedDouble) {
        Double median;
        if (sortedDouble.size() % 2 != 0) {
            median = sortedDouble.get(sortedDouble.size() / 2);
        } else {
            median = (sortedDouble.get((sortedDouble.size() - 1) / 2) + sortedDouble.get((sortedDouble.size() / 2)));
        }
        return median;
    }

    /**
     * API to update metric with new value
     *
     * @param postedMetrics - posted metrics to be updated
     * @return updatedMetrics - Newly updated metrics
     */
    @Override
    public List<MetricItem> update(List<UpdateItemDTO> postedMetrics) throws MetricDoestNotExistException {
        List<MetricItem> updatedMetrics = new ArrayList<>();
        for (int i = 0; i < postedMetrics.size(); i++) {
            UUID metricId = postedMetrics.get(i).getId();
            if (store.get(metricId) != null) {
                List<Double> values = store.get(metricId).getValues();
                values.add(Double.valueOf(postedMetrics.get(i).getValue()));
                updatedMetrics.add(new MetricItem(metricId, store.get(metricId).getName(), values));

                // UPDATE AVERAGE , MIN , MAX


            } else {
                throw new MetricDoestNotExistException("Metric Does not  Exist");
            }
        }
        return updatedMetrics;
    }


    public void clear() {
        store.clear();
    }


}
