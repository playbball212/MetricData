package com.metrics.api.repository;


import com.metrics.api.datatransferobjects.SaveItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.model.SummaryStatistics;

import java.util.List;


public interface MetricRepository {

    List<MetricItem> save(List<SaveItemDTO> metric) throws MetricAlreadyExistsException;

    MetricItem find(String id) throws MetricDoestNotExistException;


    List<MetricItem> update( List<UpdateItemDTO> metricItems) throws MetricDoestNotExistException;
}
