package com.metrics.api.repositorytests;

import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.repository.CustomMetricRepository;
import com.metrics.api.repository.MetricAlreadyExistsException;
import com.metrics.api.repository.MetricDoestNotExistException;
import com.metrics.api.repository.MetricRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.DoubleSummaryStatistics;
import java.util.UUID;

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
    @Test
    public void save_metric() throws MetricAlreadyExistsException {
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "200.00");
        MetricItem metricItem = customMetricRepository.save(metricItemDTO);
        assertThat(metricItem).isNotNull();
    }

    /**
     * Test Save  Call with invalid DOUBLE VALUE / NON HAPPY PATH
     *
     * @throws MetricDoestNotExistException
     * @throws MetricAlreadyExistsException
     */
    @Test
    public void save_metric_non_happy_path_invalid_double() {
        Exception exception = assertThrows(NumberFormatException.class, () -> {
            MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "asd");
            MetricItem metricItem = customMetricRepository.save(metricItemDTO);
        });

        assertThat(exception.getMessage()).isNotNull();
    }


    /**
     * Test Retrieving a metric by an INVALID  UUID  / non happy path
     *
     * @throws MetricAlreadyExistsException
     */
    @Test
    public void retrieve_metric_invalid_uuid_non_happy_path() throws MetricAlreadyExistsException {
        UUID uuid = UUID.randomUUID();
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
        MetricItem metricItem = customMetricRepository.save(metricItemDTO);

        // Retrieve Metric
        MetricItem retrievedMetric = customMetricRepository.find(metricItem.getId().toString());
        assertThat(retrievedMetric).isEqualTo(metricItem);
    }

    /**
     * Test API with non valid Double for metric saving
     *
     * @throws MetricAlreadyExistsException
     */
    @Test
    public void save_metric_non_valid_double_non_happy_path() throws MetricAlreadyExistsException {

        Exception exception = assertThrows(NumberFormatException.class, () -> {
            MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "asd");
            MetricItem metricItem = customMetricRepository.save(metricItemDTO);
        });

        assertThat(exception.getMessage()).isNotNull();

    }

    /**
     * Test API for saving a metric with a name that already exists
     *
     * @throws MetricAlreadyExistsException
     */
    @Test
    public void save_metric_already_exists_non_happy_path() throws MetricAlreadyExistsException {

        Exception exception = assertThrows(NumberFormatException.class, () -> {
            MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "asd");
            MetricItem metricItem = customMetricRepository.save(metricItemDTO);
        });

        assertThat(exception.getMessage()).isNotNull();

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
        MetricItem metricItem = customMetricRepository.save(metricItemDTO);
        MetricItemDTO metricItemDTO1 = new MetricItemDTO("Apple", "210.00");

        // Update Metric
        customMetricRepository.update(metricItem.getId().toString(), metricItemDTO1);

        // Retrieve Metric and assert that the size is 2
        MetricItem retrievedMetric = customMetricRepository.find(metricItem.getId().toString());
        assertThat(retrievedMetric.getValues().size()).isEqualTo(2);


    }

    /**
     * Update Metric With an Invalid UUID should throw MetricDoestNotExistException
     *
     * @throws MetricDoestNotExistException
     * @throws MetricAlreadyExistsException
     */
    @Test
    public void update_metric_invalid_uuid() throws MetricDoestNotExistException, MetricAlreadyExistsException {

        // Save Metric
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "200.00");
        MetricItem metricItem = customMetricRepository.save(metricItemDTO);
        MetricItemDTO metricItemDTO1 = new MetricItemDTO("Apple", "210.00");
        UUID randomUUID = UUID.randomUUID();
        MetricItem retrievedItem = null;
        // Update Metric
        Exception exception = assertThrows(MetricDoestNotExistException.class, () -> {
            customMetricRepository.update(randomUUID.toString(), metricItemDTO1);

        });


    }


    @Test
    public void get_summary_statistics() throws MetricDoestNotExistException, MetricAlreadyExistsException {

        // Save Metric
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "200.00");
        MetricItem metricItem = customMetricRepository.save(metricItemDTO);
        MetricItemDTO metricItemDTO1 = new MetricItemDTO("Apple", "210.00");

        // Update Metric
        customMetricRepository.update(metricItem.getId().toString(), metricItemDTO1);

        DoubleSummaryStatistics statistics = customMetricRepository.findStatsForMetric(metricItem.getId().toString());

        assertThat(statistics).isNotNull();

    }

}
