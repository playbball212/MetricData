package com.metrics.api.controllertests;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.metrics.api.controller.MetricItemController;
import com.metrics.api.datatransferobjects.SaveItemDTO;
import com.metrics.api.datatransferobjects.UpdateItemDTO;
import com.metrics.api.model.MetricItem;
import com.metrics.api.model.SummaryStatistics;
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
     * Should return 201  given the valid values  -  Double value given)
     */
    @Test
    public void register_metric() throws Exception {

        SaveItemDTO saveItemDTO = new SaveItemDTO("Apple", "200.00");
        List<Double> values = new ArrayList<>(Arrays.asList(Double.valueOf(saveItemDTO.getValue())));

        MetricItem metricItem = new MetricItem(UUID.randomUUID(), saveItemDTO.getName(), values);
        List<SaveItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(saveItemDTO));
        List<MetricItem> createdMetrics = new ArrayList<>(Arrays.asList(metricItem));
        given(metricRepository.save(new ArrayList<SaveItemDTO>(Arrays.asList(saveItemDTO)))).willReturn(createdMetrics);

        mockMvc.perform(post("/metrics")
                .content(asJsonString(postedMetrics))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())

                .andExpect(status().isCreated());

        Mockito.verify(metricRepository, times(1)).save(any(List.class));
    }


    /**
     * TEST API TO REGISTER_METRIC_NON_HAPPY_PATH
     * Should return 400  given the invalid values ( Metric Name does not already exist / NON DOUBLE value given)
     */
    @Test
    public void register_metric_non_happy_path() throws Exception {

        SaveItemDTO saveItemDTO = new SaveItemDTO("Apple", "200.00");

        List<SaveItemDTO> postedMetrics = new ArrayList<>(Arrays.asList(saveItemDTO));


        given(metricRepository.save(new ArrayList<SaveItemDTO>(Arrays.asList(saveItemDTO)))).willThrow(NumberFormatException.class);

        mockMvc.perform(post("/metrics")
                .content(asJsonString(postedMetrics))
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
     * TEST  API_TO_Update_Metric_HAPPY_PATH ( Given a Valid UUID / Valid Double Value)
     * Should Return 200 and the Metric Repository should have been called once
     */
    @Test
    public void update_metric_uuid_invalid_non_happy_path() throws Exception, MetricDoestNotExistException {
        UUID metricId = UUID.randomUUID();
        UpdateItemDTO metricItemDTO = new UpdateItemDTO(metricId, "123.00");
        List<UpdateItemDTO> updateItemDTOS = new ArrayList<>(Arrays.asList(metricItemDTO));

        given(metricRepository.update(updateItemDTOS)).willThrow(MetricDoestNotExistException.class);

        mockMvc.perform(put("/metrics")
                .content(asJsonString(updateItemDTOS))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());



    }


    /**
     * TEST  API_TO_RETRIEVE_METRIC_DETAILS_NON_HAPPY_PATH
     * Should Return MetricItem
     */
    @Test
    public void find_metric_non_hapy_path_uuid_does_not_exist() throws Exception {
        UUID metricId = UUID.randomUUID();


        given(metricRepository.find(metricId.toString())).willThrow(MetricDoestNotExistException.class);

        mockMvc.perform(get("/metrics/" + metricId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())

                .andExpect(status().isNotFound());


    }

    /**
     * Test API for getting Summary Statistics - It should return a List of SummaryStatistics
     *
     * @throws MetricDoestNotExistException
     */
    @Test
    public void find_summary_statistics_uuid_exists() throws MetricDoestNotExistException {

        UUID metricId = UUID.randomUUID();
        List<String> uuids = new ArrayList<>(Arrays.asList(metricId.toString()));
        SummaryStatistics summaryStat = new SummaryStatistics(22.0, 22.0, 22.0, 22.0, metricId.toString());


        List<SummaryStatistics> groupStatistics = new ArrayList<>(Arrays.asList(summaryStat));
        given(metricRepository.findStatsForMetric(uuids)).willReturn(groupStatistics);
        try {
            mockMvc.perform(get("/metrics/summarystatistics")
                    .content(asJsonString(uuids))

                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))

                    .andDo(print())

                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Test API for getting Summary Statistics - UUID DOES NOT EXIST SHOULD GET BAD REQUEST
     *
     * @throws MetricDoestNotExistException
     */
    @Test
    public void find_summary_statistics_uuid_non_happy_doesnt_exist() throws MetricDoestNotExistException {

        UUID metricId = UUID.randomUUID();
        List<String> uuids = new ArrayList<>(Arrays.asList(metricId.toString()));

        Map<Integer,Integer> map = new HashMap<>();



        given(metricRepository.findStatsForMetric(uuids)).willThrow(MetricDoestNotExistException.class);
        try {
            mockMvc.perform(get("/metrics/summarystatistics")
                    .content(asJsonString(uuids))

                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))

                    .andDo(print())

                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
