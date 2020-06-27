package com.metrics.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryStatistics {

    private Double mean;
    private Double median;
    private Double min;
    private Double max;
    private String id;
}
