package com.metrics.api.controllertests;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.metrics.api.controller.MetricItemController;
import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.repository.MetricAlreadyExistsException;
import com.metrics.api.repository.MetricDoestNotExistException;
import com.metrics.api.repository.MetricRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MetricItemController.class)
public class MetricItemControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private MetricRepository metricRepository;


    /**
     * TEST API TO REGISTER_METRIC_HAPPY_PATH
     * Should return 201  given the valid values ( Metric Name does not already exist / Double value given)
     */
    @Test
    public void register_metric() throws Exception {

        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "200.00");
        UUID metricId = UUID.randomUUID();
        List<Double> values = new ArrayList<>(Arrays.asList(Double.valueOf(metricItemDTO.getValue())));
        MetricItem metricItem = new MetricItem(metricId, metricItemDTO.getName(), values);
        given(metricRepository.save(metricItemDTO)).willReturn(metricItem);

        mockMvc.perform(post("/metrics")
                .content(asJsonString(metricItemDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.values").isArray())
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(status().isCreated());

        Mockito.verify(metricRepository, times(1)).save(any(MetricItemDTO.class));
    }

    /**
     * TEST API TO REGISTER_METRIC_NON_HAPPY_PATH ( Non Double Value Scenario)
     * Should Return BAD REQUEST STATUS 400
     */
    @Test
    public void register_metric_non_happy_path() throws Exception {

        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "ABC");

        UUID metricId = UUID.randomUUID();

        given(metricRepository.save(any(MetricItemDTO.class))).willThrow(NumberFormatException.class);

        mockMvc.perform(post("/metrics")
                .content(asJsonString(metricItemDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    /**
     * TEST API TO REGISTER_METRIC_NON_HAPPY_PATH ( Metric Already Exists)
     * Should Return BAD REQUEST STATUS 400
     */
    @Test
    public void register_metric_arleady_exists_non_happy_path() throws Exception {

        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "ABC");

        UUID metricId = UUID.randomUUID();

        given(metricRepository.save(metricItemDTO)).willThrow(MetricAlreadyExistsException.class);

        mockMvc.perform(post("/metrics")
                .content(asJsonString(metricItemDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


    /**
     * TEST  API_TO_Update_Metric_HAPPY_PATH ( Given a Valid UUID / Valid Double Value)
     * Should Return 200 and the Metric Repository should have been called once
     */
    @Test
    public void update_metric() throws Exception, MetricDoestNotExistException {
        UUID metricId = UUID.randomUUID();
        List<Double> values = new ArrayList<Double>(Arrays.asList(232.300));
        MetricItem metricItem = new MetricItem(metricId, "Apple", values);
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "123.00");


        given(metricRepository.update(metricId.toString(), metricItemDTO)).willReturn(metricItem);

        mockMvc.perform(put("/metrics/" + metricId)
                .content(asJsonString(metricItemDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(metricRepository, times(1)).update(any(String.class), any(MetricItemDTO.class));


    }

    /**
     * TEST  API_TO_Update_Metric_NON_HAPPY_PATH ( Given a NON Valid UUID / Valid Double Value)
     * Should Return 400
     */
    @Test
    public void update_metric_non_happy_path() throws Exception, MetricDoestNotExistException {
        UUID metricId = UUID.randomUUID();
        List<Double> values = new ArrayList<Double>(Arrays.asList(232.300));
        MetricItem metricItem = new MetricItem(metricId, "Apple", values);
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "123.00");


        given(metricRepository.update(metricId.toString(), metricItemDTO)).willThrow(NumberFormatException.class);

        mockMvc.perform(put("/metrics/" + metricId)
                .content(asJsonString(metricItemDTO))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());


    }


    /**
     * TEST  API_TO_RETRIEVE_METRIC_DETAILS_HAPPY_PATH
     * Should Return MetricItem
     */
    @Test
    public void find_metric() throws Exception, MetricDoestNotExistException {
        UUID metricId = UUID.randomUUID();
        List<Double> values = new ArrayList<Double>(Arrays.asList(232.300));
        MetricItem metricItem = new MetricItem(metricId, "Apple", values);
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "123.00");


        given(metricRepository.find(metricId.toString())).willReturn(metricItem);

        mockMvc.perform(get("/metrics/" + metricId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.name").value("Apple"))

                .andExpect(status().isOk());


        Mockito.verify(metricRepository, times(1)).find(metricId.toString());


    }


    /**
     * TEST  API_TO_RETRIEVE_METRIC_DETAILS_NON_HAPPY_PATH
     * Should Return MetricItem
     */
    @Test
    public void find_metric_non_hapy_path_uuid_does_not_exist() throws Exception {
        UUID metricId = UUID.randomUUID();
        List<Double> values = new ArrayList<Double>(Arrays.asList(232.300));
        MetricItem metricItem = new MetricItem(metricId, "Apple", values);
        MetricItemDTO metricItemDTO = new MetricItemDTO("Apple", "123.00");


        given(metricRepository.find(metricId.toString())).willThrow(MetricDoestNotExistException.class);

        mockMvc.perform(get("/metrics/" + metricId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())

                .andExpect(status().isNotFound());


    }

    /**
     * TEST  API_TO_GET_SUMMARY_STATISTICS HAPPY PATH ( VALID UUID)
     * Should Return 200 AND SummaryResponse
     */
    @Test
    public void find_summarystats() throws Exception, MetricDoestNotExistException {
        UUID metricId = UUID.randomUUID();

        DoubleSummaryStatistics doubleSummaryStatistics = new DoubleSummaryStatistics();
        doubleSummaryStatistics.accept(22.00);
        doubleSummaryStatistics.accept(25.00);
        doubleSummaryStatistics.accept(23.00);

        given(metricRepository.findStatsForMetric(metricId.toString())).willReturn(doubleSummaryStatistics);

        mockMvc.perform(get("/metrics/summarystatistics/" + metricId)

                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.average").value("23.333333333333332"))
                .andExpect(jsonPath("$.min").value("22.0"))
                .andExpect(jsonPath("$.max").value("25.0"))

                .andExpect(status().isOk());


    }


    /**
     * TEST  API_TO_GET_SUMMARY_STATISTICS NON_HAPPY_PATH ( VALID UUID)
     * Should Return 200 AND SummaryResponse
     */
    @Test
    public void find_summarystats_non_happy_path_uuid_doesnt_exist() throws Exception {
        UUID metricId = UUID.randomUUID();

        DoubleSummaryStatistics doubleSummaryStatistics = new DoubleSummaryStatistics();
        doubleSummaryStatistics.accept(22.00);
        doubleSummaryStatistics.accept(25.00);
        doubleSummaryStatistics.accept(23.00);

        given(metricRepository.findStatsForMetric(metricId.toString())).willThrow(MetricDoestNotExistException.class);

        mockMvc.perform(get("/metrics/summarystatistics/" + metricId)

                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())


                .andExpect(status().isNotFound());


    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
