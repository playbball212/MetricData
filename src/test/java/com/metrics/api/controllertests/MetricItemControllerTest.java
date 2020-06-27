package com.metrics.api.controllertests;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.metrics.api.controller.MetricItemController;
import com.metrics.api.datatransferobjects.MetricItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
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
        List<Double> values = new ArrayList<>(Arrays.asList(Double.valueOf(metricItemDTO.getValue())));

        MetricItem metricItem = new MetricItem(UUID.randomUUID(), metricItemDTO.getName(), values);
        List<MetricItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(metricItemDTO));
        List<MetricItem> createdMetrics = new ArrayList<>(Arrays.asList(metricItem));
        given(metricRepository.save(new ArrayList<MetricItemDTO>(Arrays.asList(metricItemDTO)))).willReturn(createdMetrics);

        mockMvc.perform(post("/metrics")
                .content(asJsonString(postedMetrics))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())

                .andExpect(status().isCreated());

        Mockito.verify(metricRepository, times(1)).save(any(List.class));
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
        List<MetricItem> metricItemList = new ArrayList<>(Arrays.asList(metricItem));
        UpdateItemDTO metricItemDTO = new UpdateItemDTO(metricId, "123.00");
        List<UpdateItemDTO> updateItemDTOS = new ArrayList<>(Arrays.asList(metricItemDTO));

        given(metricRepository.update(updateItemDTOS)).willReturn(metricItemList);

        mockMvc.perform(put("/metrics")
                .content(asJsonString(updateItemDTOS))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(metricRepository, times(1)).update(updateItemDTOS);


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
