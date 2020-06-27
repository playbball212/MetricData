package com.metrics.api.repositorytests;

import com.metrics.api.datatransferobjects.SaveItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.model.SummaryStatistics;
import com.metrics.api.repository.CustomMetricRepository;
import com.metrics.api.repository.MetricAlreadyExistsException;
import com.metrics.api.repository.MetricDoestNotExistException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

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
        SaveItemDTO saveItemDTO = new SaveItemDTO("Apple", "200.00");
        List<SaveItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(saveItemDTO));
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
        SaveItemDTO saveItemDTO2 = new SaveItemDTO("Apple", "200.00");
        List<SaveItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(saveItemDTO2));

        List<MetricItem> metricItem2 = customMetricRepository.save(postedMetrics);
        Exception exception = assertThrows(MetricDoestNotExistException.class, () -> {
            SaveItemDTO saveItemDTO = new SaveItemDTO("Apple", "asd");
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
        SaveItemDTO saveItemDTO = new SaveItemDTO("Apple", "200.00");
        List<SaveItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(saveItemDTO));

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
        List<MetricItem> metricItemList = saveAndUpdateMetric();

        // Retrieve Metric and assert that the size is 2
        MetricItem retrievedMetric = customMetricRepository.find(metricItemList.get(0).getId().toString());
        assertThat(retrievedMetric.getValues().size()).isEqualTo(7);


    }

    // Helper Method
    private List<MetricItem> saveAndUpdateMetric() throws MetricDoestNotExistException {
        // Save Metric
        SaveItemDTO saveItemDTO = new SaveItemDTO("Apple", "1.00");
        SaveItemDTO saveItemDTO2 = new SaveItemDTO("Ford", "1.00");
        SaveItemDTO saveItemDTO3 = new SaveItemDTO("Microsoft", "1.00");
        SaveItemDTO saveItemDTO4 = new SaveItemDTO("Microsoft", "2.00");

        List<SaveItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(saveItemDTO, saveItemDTO2, saveItemDTO3, saveItemDTO4));

        List<MetricItem> metricItems = customMetricRepository.save(postedMetrics);
        UpdateItemDTO metricItemDTO1 = new UpdateItemDTO(metricItems.get(0).getId(), "3.00");
        UpdateItemDTO metricItemDTO2 = new UpdateItemDTO(metricItems.get(0).getId(), "3.00");
        UpdateItemDTO metricItemDTO3 = new UpdateItemDTO(metricItems.get(0).getId(), "6.00");
        UpdateItemDTO metricItemDTO4 = new UpdateItemDTO(metricItems.get(0).getId(), "7.00");
        UpdateItemDTO metricItemDTO5 = new UpdateItemDTO(metricItems.get(0).getId(), "8.00");
        UpdateItemDTO metricItemDTO6 = new UpdateItemDTO(metricItems.get(0).getId(), "9.00");

        UpdateItemDTO metricItemDTO9 = new UpdateItemDTO(metricItems.get(1).getId(), "1.00");
        UpdateItemDTO metricItemDTO10 = new UpdateItemDTO(metricItems.get(1).getId(), "1.00");
        UpdateItemDTO metricItemDTO11 = new UpdateItemDTO(metricItems.get(3).getId(), "1.00");


        UpdateItemDTO metricItemDTO7 = new UpdateItemDTO(metricItems.get(2).getId(), "8.00");
        UpdateItemDTO metricItemDTO8 = new UpdateItemDTO(metricItems.get(2).getId(), "9.00");


        List<UpdateItemDTO> updateItemDTOList = new ArrayList<>(Arrays.asList(metricItemDTO1, metricItemDTO2,
                metricItemDTO3, metricItemDTO4, metricItemDTO5, metricItemDTO6, metricItemDTO7, metricItemDTO8,
                metricItemDTO9, metricItemDTO10, metricItemDTO11));
        // Update Metric

        customMetricRepository.update(updateItemDTOList);

        return metricItems;
    }

    /**
     * Summary Statistics Should return correct min , max , mean
     *
     * @throws MetricDoestNotExistException
     */
    @Test
    public void find_summary_statistics_valid_uuid() throws MetricDoestNotExistException {

        List<MetricItem> postedMetrics = saveAndUpdateMetric();
        List<String> uuids = new ArrayList<>(Arrays.asList(postedMetrics.get(0).getId().toString(),
                postedMetrics.get(1).getId().toString(), postedMetrics.get(2).getId().toString(),
                postedMetrics.get(3).getId().toString()));

        List<SummaryStatistics> summaryStatistics = customMetricRepository.findStatsForMetric(uuids);

        assertThat(summaryStatistics).isNotNull();
        assertThat(summaryStatistics.get(0).getMin()).isEqualTo(1.00);
        assertThat(summaryStatistics.get(0).getMax()).isEqualTo(9.00);
        assertThat(summaryStatistics.get(0).getMean()).isEqualTo(5.285714285714286);


        assertThat(summaryStatistics.get(1).getMin()).isEqualTo(1.00);
        assertThat(summaryStatistics.get(1).getMax()).isEqualTo(1.00);
        assertThat(summaryStatistics.get(1).getMean()).isEqualTo(1.00);

        assertThat(summaryStatistics.get(2).getMin()).isEqualTo(1.00);
        assertThat(summaryStatistics.get(2).getMax()).isEqualTo(9.00);
        assertThat(summaryStatistics.get(2).getMean()).isEqualTo(6.00);


    }


}
