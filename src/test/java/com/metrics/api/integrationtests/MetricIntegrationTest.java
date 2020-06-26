package com.metrics.api.integrationtests;

import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.repository.MetricRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MetricIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MetricRepository customMetricRepository;

    /**
     * IF I REGISTER A METRIC THAT DOES NOT EXIST AND HAS A VALID DOUBLE VALUE
     * RESPONSE SHOULD CONTAIN METRIC DATA / STATUS SHOULD BE 201
     */
    @Test
    public void register_metric() {

        MetricItemDTO metricItemDTO = new MetricItemDTO("Ford", "200.00");

        ResponseEntity<MetricItem> response = testRestTemplate.postForEntity("/metrics", metricItemDTO, MetricItem.class);

        MetricItem metricItem = response.getBody();

        assertThat(metricItem.getId()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);


    }

    /**
     * IF I REGISTER A METRIC WITH A NAME THAT ALREADY EXISTS
     * RETURN 400 BAD REQUEST
     */
    @Test
    public void register_metric_non_happy_path() {

        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "200.00");
        testRestTemplate.postForEntity("/metrics", metricItemDTO, MetricItem.class);


        ResponseEntity<MetricItem> response = testRestTemplate.postForEntity("/metrics", metricItemDTO, MetricItem.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);


    }

    /**
     * IF I REGISTER A METRIC THAT DOES NOT EXIST AND HAS A VALID DOUBLE VALUE = Metric Should be Created ( Validated Earlier)
     * IF I UPDATE A METRIC THAT EXISTS THEN THE LIST OF VALUES SHOULD BE UPDATED.
     */
    @Test
    public void update_metric() {

        MetricItemDTO metricItemDTO = new MetricItemDTO("Facebook", "200.00");
        ResponseEntity<MetricItem> responseEntity = testRestTemplate.postForEntity("/metrics", metricItemDTO, MetricItem.class);

        MetricItem metricItem = responseEntity.getBody();

        // Size Originally will be One
        assertThat(metricItem.getValues()).size().isEqualTo(1);

        MetricItemDTO metricItemDTO1 = new MetricItemDTO("Facebook", "210.00");
        testRestTemplate.put("/metrics/" + metricItem.getId(), metricItemDTO, MetricItem.class);


        ResponseEntity<MetricItem> retrieviedMetricItem = testRestTemplate.getForEntity("/metrics/" + metricItem.getId(), MetricItem.class);

        MetricItem updatedItem = retrieviedMetricItem.getBody();

        // Size will be Two Now
        assertThat(updatedItem.getValues().size()).isEqualTo(2);


    }


    /**
     *
     * IF I UPDATE A METRIC THAT  DOES NOT EXISTS THEN THE LIST OF VALUES SHOULD BE NOT  BE UPDATED
     */
    @Test
    public void update_metric_non_happy_path() {

        MetricItemDTO metricItemDTO = new MetricItemDTO("Netflix", "200.00");
        ResponseEntity<MetricItem> responseEntity = testRestTemplate.postForEntity("/metrics", metricItemDTO, MetricItem.class);

        MetricItem metricItem = responseEntity.getBody();

        // Size Originally will be One
        assertThat(metricItem.getValues()).size().isEqualTo(1);

        MetricItemDTO metricItemDTO1 = new MetricItemDTO("Netflix", "210.00");
        testRestTemplate.put("/metrics/" + UUID.randomUUID(), metricItemDTO, MetricItem.class);


        ResponseEntity<MetricItem> retrieviedMetricItem = testRestTemplate.getForEntity("/metrics/" + metricItem.getId(), MetricItem.class);

        MetricItem updatedItem = retrieviedMetricItem.getBody();

        // SIZE SHOULD NOT BE UPDATED
        assertThat(updatedItem.getValues().size()).isEqualTo(1);


    }


}
