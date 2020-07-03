package com.metrics.api.controller;


import com.metrics.api.constants.ErrorCodes;
import com.metrics.api.datatransferobjects.SaveItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.model.SummaryStatistics;
import com.metrics.api.repository.MetricAlreadyExistsException;
import com.metrics.api.repository.MetricDoestNotExistException;
import com.metrics.api.repository.MetricRepository;
import com.metrics.api.repository.StatsRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@AllArgsConstructor
public class MetricItemController {


    private final MetricRepository customMetricRepository;


    /**
     * API To Save Metrics
     *
     * @param saveItemDTO
     * @param response
     * @return savedMetrics - Newly Saved Metrics
     */
    @PostMapping("/metrics")
    public List<MetricItem> saveMetric(@Valid @RequestBody List<SaveItemDTO> saveItemDTO, HttpServletResponse response) {
        List<MetricItem> savedMetrics = null;

        try {

            savedMetrics = customMetricRepository.save(saveItemDTO);
            response.setStatus(201);
        } catch (NumberFormatException | MetricAlreadyExistsException e) {


            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

        return savedMetrics;

    }


    /**
     * API to retrieve details about a particular Metric
     *
     * @param id
     */
    @GetMapping("/metrics/{id}")
    public MetricItem helperMethod(@PathVariable String id) {

        try {
            return customMetricRepository.find(id);
        } catch (IllegalArgumentException | NullPointerException | MetricDoestNotExistException e) {

            if (e instanceof MetricDoestNotExistException) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Metric does not exist", e);
            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, ErrorCodes.UUID_NOT_FOUND, e);
            }
        }
    }


    /**
     * API To Retrieve Summary Statistics on metrics specified in request
     *
     * @param metricSummary
     * @return List<SummaryStatistics> Summary Statistics for Metrics Specified </SummaryStatistics>
     */
    @GetMapping("/metrics/summarystatistics")
    public List<SummaryStatistics> getSummaryStatistics(@RequestBody List<String> metricSummary) {

        List<SummaryStatistics> summaryStatistics = null;
        try {
            summaryStatistics = customMetricRepository.findStatsForMetric(metricSummary);
        } catch (MetricDoestNotExistException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorCodes.UUID_NOT_FOUND, e);

        }
        return summaryStatistics;
    }

    /**
     * API to update Metric based on new double values
     *
     * @param metricItems MetricItemDTO ( persistent enties should not be used as requestbody)
     * @return updatedMetricItemList - List of Updated Values
     */
    @PutMapping("/metrics")
    public List<MetricItem> updateMetric(@RequestBody List<UpdateItemDTO> metricItems) {

        List<MetricItem> updatedMetricItemList = null;

        try {
            updatedMetricItemList = customMetricRepository.update(metricItems);


        } catch (MetricDoestNotExistException | NumberFormatException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorCodes.UUID_NOT_FOUND, e);

        }
        return updatedMetricItemList;
    }


}
