package com.metrics.api.controller;


import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.repository.MetricAlreadyExistsException;
import com.metrics.api.repository.MetricDoestNotExistException;
import com.metrics.api.repository.MetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.DoubleSummaryStatistics;

@RestController
public class MetricItemController {


    public MetricRepository customMetricRepository;

    @Autowired
    public MetricItemController(MetricRepository customMetricRepository) {
        this.customMetricRepository = customMetricRepository;
    }


    /**
     * API to create a metric
     */
    @PostMapping("/metrics")
    public MetricItem saveMetric(@RequestBody MetricItemDTO metricItemDTO, HttpServletResponse response) {
        MetricItem item = null;

        try {
            item = customMetricRepository.save(metricItemDTO);
            if (item.getId() != null) {
                response.setStatus(201);
            }
        } catch (NullPointerException | NumberFormatException | MetricAlreadyExistsException e) {


            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

        return item;

    }

    /**
     * API to retrieve a metric
     */
    @GetMapping("/metrics/{id}")
    public MetricItem getMetric(@PathVariable String id, HttpServletResponse response) {

        try {
            return customMetricRepository.find(id);
        } catch (NullPointerException | MetricDoestNotExistException | NumberFormatException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The UUID you entered could not be found", e);

        }
    }

    /**
     * API to retrieve Summary Statistics
     *
     * @param id       UUID of Metric
     * @param response Summary Statics about the specified metric
     * @return
     */
    @GetMapping("/metrics/summarystatistics/{id}")
    public DoubleSummaryStatistics getSummaryStatistics(@PathVariable String id, HttpServletResponse response) {

        DoubleSummaryStatistics summaryStatistics = null;
        try {
            summaryStatistics = customMetricRepository.findStatsForMetric(id);
        } catch (NullPointerException | MetricDoestNotExistException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The UUID you entered could not be found", e);

        }
        return summaryStatistics;
    }

    /**
     * API to update a metric
     */
    @PutMapping("/metrics/{id}")
    public MetricItem updateMetric(@PathVariable String id, @RequestBody MetricItemDTO metricItemDTO, HttpServletResponse response) throws IOException {

        MetricItem item = null;

        try {
            item = customMetricRepository.update(id, metricItemDTO);


        } catch (MetricDoestNotExistException | NumberFormatException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "The UUID you entered could not be found", e);

        }
        return item;
    }


}
