package com.metrics.api.controller;


import com.metrics.api.constants.ErrorCodes;
import com.metrics.api.datatransferobjects.SaveItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.model.SummaryStatistics;
import com.metrics.api.repository.MetricAlreadyExistsException;
import com.metrics.api.repository.MetricDoestNotExistException;
import com.metrics.api.repository.MetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class MetricItemController {


    public MetricRepository customMetricRepository;

    @Autowired
    public MetricItemController(MetricRepository customMetricRepository) {
        this.customMetricRepository = customMetricRepository;
    }


    /**
     * API to Register Metric
     *
     * @param saveItemDTO
     * @param response    MetricItem ( Name , List of  Double , UUID)
     * @return
     */
    @PostMapping("/metrics")
    public List<MetricItem> saveMetric(@RequestBody List<SaveItemDTO> saveItemDTO, HttpServletResponse response) {
        List<MetricItem> item = null;

        try {
            item = customMetricRepository.save(saveItemDTO);
            response.setStatus(201);
        } catch (NullPointerException | NumberFormatException | MetricAlreadyExistsException e) {


            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

        return item;

    }


    /**
     * API to retrieve details about a particular Metric
     *
     * @param id
     * @param response MetricItem ( Name , UUID , List of Double Values)
     * @return Details about a Particular Metric
     */
    @GetMapping("/metrics/{id}")
    public MetricItem helperMethod(@PathVariable String id, HttpServletResponse response) {

        try {
            return customMetricRepository.find(id);
        } catch (NullPointerException | MetricDoestNotExistException | NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorCodes.UUID_NOT_FOUND, e);

        }
    }


    /**
     * API to retrieve Summary Statistics
     *
     * @param metricSummary of Metric
     * @param response      Summary Statics about the specified metric
     * @return
     */
    @GetMapping("/metrics/summarystatistics")
    public List<SummaryStatistics> getSummaryStatistics(@RequestBody List<String> metricSummary, HttpServletResponse response) {

        List<SummaryStatistics> summaryStatistics = null;
        try {
            summaryStatistics = customMetricRepository.findStatsForMetric(metricSummary);
        } catch (NullPointerException | MetricDoestNotExistException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorCodes.UUID_NOT_FOUND, e);

        }
        return summaryStatistics;
    }

    /**
     * API to update Metric based on new double values
     *
     * @param
     * @param metricItems MetricItemDTO ( persistent enties should not be used as requestbody)
     * @return MetricItem item - Updated with new values
     * @throws IOException
     */
    @PutMapping("/metrics")
    public List<MetricItem> updateMetric(@RequestBody List<UpdateItemDTO> metricItems) {

        List<MetricItem> item = null;

        try {
            item = customMetricRepository.update(metricItems);


        } catch (MetricDoestNotExistException | NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorCodes.UUID_NOT_FOUND, e);

        }
        return item;
    }


}
