package com.metrics.api.repositorytests;

import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.repository.CustomMetricRepository;
import com.metrics.api.repository.MetricAlreadyExistsException;
import com.metrics.api.repository.MetricDoestNotExistException;
import com.metrics.api.repository.MetricRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class MetricItemRepositoryTest {

    private CustomMetricRepository customMetricRepository = new CustomMetricRepository();


    /**
     * Test Service Call for saving Metric with valid Double Value.
     *
     * @throws MetricAlreadyExistsException
     */

    @BeforeEach()
    public void cleanupBeforeTest() {
        customMetricRepository.clear();
    }

    @Test
    public void save_metric() throws MetricAlreadyExistsException {
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "200.00");
        List<MetricItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(metricItemDTO));
        List<MetricItem> metricItem = customMetricRepository.save(postedMetrics);
        assertThat(metricItem.size()).isEqualTo(1);
    }


    /**
     * Test Retrieving a metric by an INVALID  UUID  / non happy path
     *
     * @throws MetricAlreadyExistsException
     */
    @Test
    public void retrieve_metric_invalid_uuid_non_happy_path() throws MetricAlreadyExistsException {
        UUID uuid = UUID.randomUUID();
        // Save Metric
        MetricItemDTO metricItemDTO2 = new MetricItemDTO("Apple", "200.00");
        List<MetricItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(metricItemDTO2));

        List<MetricItem> metricItem2 = customMetricRepository.save(postedMetrics);
        Exception exception = assertThrows(MetricDoestNotExistException.class, () -> {
            MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "asd");
            MetricItem metricItem = customMetricRepository.find(uuid.toString());
        });

        assertThat(exception.getMessage()).isNotNull();

    }


    /**
     * Test Retrieving a metric by saving it first and passing the UUID to find method. VALID UUID scenario
     *
     * @throws MetricDoestNotExistException
     * @throws MetricAlreadyExistsException
     */
    @Test
    public void retrieve_metric_valid_uuid() throws MetricDoestNotExistException, MetricAlreadyExistsException {
        // Save Metric
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "200.00");
        List<MetricItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(metricItemDTO));

        List<MetricItem> metricItems = customMetricRepository.save(postedMetrics);

        UUID metricId = metricItems.get(0).getId();

        // Retrieve Metric
        MetricItem retrievedMetric = customMetricRepository.find(metricId.toString());
        assertThat(retrievedMetric.getId()).isEqualTo(metricId);
    }


    /**
     * Test API for updating Metric with new value size should be greater
     *
     * @throws MetricDoestNotExistException
     * @throws MetricAlreadyExistsException
     */
    @Test
    public void update_metric_valid_uuid() throws MetricDoestNotExistException, MetricAlreadyExistsException {

        // Save Metric
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "200.00");
        List<MetricItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(metricItemDTO));

        List<MetricItem> metricItems = customMetricRepository.save(postedMetrics);
        UpdateItemDTO metricItemDTO1 = new UpdateItemDTO(metricItems.get(0).getId(), "210.00");
        List<UpdateItemDTO> updateItemDTOList = new ArrayList<>(Arrays.asList(metricItemDTO1));
        String metricId = metricItems.get(0).getId().toString();
        // Update Metric
        customMetricRepository.update(updateItemDTOList);

        // Retrieve Metric and assert that the size is 2
        MetricItem retrievedMetric = customMetricRepository.find(metricItemDTO1.getId().toString());
        assertThat(retrievedMetric.getValues().size()).isEqualTo(2);


    }


}
