package com.metrics.api.integrationtests;

import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.repository.MetricDoestNotExistException;
import com.metrics.api.repository.MetricRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        List<MetricItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(metricItemDTO));

        ResponseEntity<MetricItem[]> response = testRestTemplate.postForEntity("/metrics", postedMetrics, MetricItem[].class);

        MetricItem[] savedMetrics = response.getBody();


        assertThat(savedMetrics).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);


    }


    /**
     * IF I REGISTER A METRIC THAT DOES NOT EXIST AND HAS A VALID DOUBLE VALUE = Metric Should be Created ( Validated Earlier)
     * IF I UPDATE A METRIC THAT EXISTS THEN THE LIST OF VALUES SHOULD BE UPDATED.
     */
    @Test
    public void update_metric() {

        MetricItemDTO metricItemDTO = new MetricItemDTO("Facebook", "200.00");
        List<MetricItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(metricItemDTO));
        ResponseEntity<MetricItem[]> responseEntity = testRestTemplate.postForEntity("/metrics", postedMetrics, MetricItem[].class);

        MetricItem[] metricItems = responseEntity.getBody();

        String metricId = metricItems[0].getId().toString();

        // Size Originally will be One
        assertThat(metricItems.length).isEqualTo(1);

        UpdateItemDTO metricItemDTO1 = new UpdateItemDTO(metricItems[0].getId(), "210.00");
        List<UpdateItemDTO> updateItemDTOList = new ArrayList<>(Arrays.asList(metricItemDTO1));

        testRestTemplate.put("/metrics", updateItemDTOList);


        ResponseEntity<MetricItem> retrievedMetricItems = testRestTemplate.getForEntity("/metrics/" + metricId, MetricItem.class);

        MetricItem updatedItem = retrievedMetricItems.getBody();

        // Size will be Two Now
        assertThat(updatedItem.getValues().size()).isEqualTo(2);


    }


}
