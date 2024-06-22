package com.global.aod.interview.techtest.controller;

import com.global.aod.interview.techtest.contoller.StationsController;
import com.global.aod.interview.techtest.model.Station;
import com.global.aod.interview.techtest.service.StationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.global.aod.interview.techtest.utils.JsonUtils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StationsController.class)
class StationsControllerTest {

    private static final String STATION_NAME = "Heart FM";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationService stationService;

    @BeforeEach
    public void setup() {
        reset(stationService);
    }

    @Test
    @DisplayName("CreateStation - Input validation is failing")
    void shouldFailValidationWhenAddingNewStation() throws Exception {
        var payload = Station.builder().build();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/stations")
                        .content(asJsonString(payload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(stationService);
    }

    @Test
    @DisplayName("CreateStation - Success")
    void shouldAddNewStation() throws Exception {
        var payload = Station.builder().stationName(STATION_NAME).build();
        var response = Station.builder().stationName(STATION_NAME).id(1L).version(0).build();

        when(stationService.createStation(any(Station.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/stations")
                        .content(asJsonString(payload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/stations/1"))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.stationName").value(STATION_NAME));
    }
}
