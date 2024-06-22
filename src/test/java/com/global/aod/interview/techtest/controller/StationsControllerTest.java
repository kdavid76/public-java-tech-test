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

import java.util.List;

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

    @Test
    @DisplayName("FindAllStations - Reply with a list of stations")
    void shouldReturnAListOfStations() throws Exception {
        var station = Station.builder().stationName(STATION_NAME).id(1L).version(0).build();

        when(stationService.findAllStations()).thenReturn(List.of(station, station, station));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/stations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("FindAllStations - No content when no stations has been persisted")
    void shouldReturnEmptyListOfStations() throws Exception {

        when(stationService.findAllStations()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/stations")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("FindById - Station found")
    void shouldReturnAStation() throws Exception {
        var station = Station.builder().stationName(STATION_NAME).id(1L).version(0).build();

        when(stationService.findById(1L)).thenReturn(station);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/stations/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.stationName").value(STATION_NAME));
    }

    @Test
    @DisplayName("FindById - Station not found")
    void shouldNotFindStation() throws Exception {

        when(stationService.findById(1L)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/stations/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("UpdateStation - Input validation is failing")
    void shouldFailValidationWhenUpdatingStation() throws Exception {
        var payload = Station.builder().build();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/stations")
                        .content(asJsonString(payload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(stationService);
    }

    @Test
    @DisplayName("UpdateStation - Success")
    void shouldUpdateStation() throws Exception {
        var payload = Station.builder().stationName(STATION_NAME).build();
        var response = Station.builder().stationName(STATION_NAME).id(1L).version(0).build();

        when(stationService.updateStation(any(Station.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/stations")
                        .content(asJsonString(payload))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.stationName").value(STATION_NAME));
    }
}
