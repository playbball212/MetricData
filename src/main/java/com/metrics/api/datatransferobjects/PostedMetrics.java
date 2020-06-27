package com.metrics.api.datatransferobjects;

import com.metrics.api.model.MetricItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostedMetrics {

    private List<MetricItem> postedMetrics;

}
