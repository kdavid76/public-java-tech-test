package com.global.aod.interview.techtest.integration;

import com.global.aod.interview.techtest.model.Station;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndIT {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";


    private static RestTemplate restTemplate = null;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port + "").concat("/stations");
    }

    @Test
    @DisplayName("End to end test with creating, retrieving, amending and deleting station data")
    void shouldWorkEndToEnd() {
        // STEP 1: Retrieve the list of stations, but there's nothing in the database
        var stationListResponse = restTemplate.getForEntity(baseUrl, Station[].class);
        assertThat(stationListResponse.getStatusCode()).isNotNull().isEqualTo(HttpStatus.NO_CONTENT);

        // STEP 2: Register a few stations
        var station1 = Station.builder().stationName("Heart UK").build();
        var station2 = Station.builder().stationName("Capital FM").build();
        var station3 = Station.builder().stationName("Radio X").build();
        var station4 = Station.builder().stationName("Kiss").build();

        var stationResponse = restTemplate.postForEntity(baseUrl, station1, Station.class);
        assertStatusCode(stationResponse, HttpStatus.CREATED);
        assertStation(stationResponse.getBody(), 1L, station1.stationName(), 0);

        stationResponse = restTemplate.postForEntity(baseUrl, station2, Station.class);
        assertStatusCode(stationResponse, HttpStatus.CREATED);
        assertStation(stationResponse.getBody(), 2L, station2.stationName(), 0);

        stationResponse = restTemplate.postForEntity(baseUrl, station3, Station.class);
        assertStatusCode(stationResponse, HttpStatus.CREATED);
        assertStation(stationResponse.getBody(), 3L, station3.stationName(), 0);

        stationResponse = restTemplate.postForEntity(baseUrl, station4, Station.class);
        assertStatusCode(stationResponse, HttpStatus.CREATED);
        assertStation(stationResponse.getBody(), 4L, station4.stationName(), 0);

        // STEP 3: Retrieve the list of stations for checking the number of available ones
        stationListResponse = restTemplate.getForEntity(baseUrl, Station[].class);
        assertStatusCode(stationListResponse, HttpStatus.OK);
        assertThat(stationListResponse.getBody()).isNotNull().hasSize(4);

        // STEP 4: Get only one selected Station
        var singleStationResponse = restTemplate.getForEntity(baseUrl.concat("/2"), Station.class);
        assertStatusCode(singleStationResponse, HttpStatus.OK);
        assertStation(singleStationResponse.getBody(), 2L, station2.stationName(), 0);

        // STEP 5: Amend a station
        var stationToAmend = Station.builder()
                .stationName("Modified FM")
                .id(Objects.requireNonNull(singleStationResponse.getBody()).id())
                .version(singleStationResponse.getBody().version())
                .build();
        var amendResponse = restTemplate.exchange(baseUrl, HttpMethod.PUT, buildHttpEntity(stationToAmend), Station.class);
        assertStatusCode(amendResponse, HttpStatus.OK);
        assertStation(amendResponse.getBody(), stationToAmend.id(), stationToAmend.stationName(), stationToAmend.version() + 1);

        // STEP 6: Delete a station
        var deleteResponse = restTemplate.exchange(baseUrl + "/3", HttpMethod.DELETE, buildHttpEntity(null), Void.class);
        assertStatusCode(deleteResponse, HttpStatus.NO_CONTENT);

        // STEP 7: Check if only 3 stations left
        try {
            restTemplate.getForEntity(baseUrl.concat("/3"), Station.class);
            fail("This should throw a HttpClientErrorException.");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
        stationListResponse = restTemplate.getForEntity(baseUrl, Station[].class);
        assertStatusCode(stationListResponse, HttpStatus.OK);
        assertThat(stationListResponse.getBody()).isNotNull().hasSize(3);


    }

    @Test
    @DisplayName("Optimistic locking exception should be thrown when concurrent modifications")
    void shouldThrowOptimisticLockingException() {

        // STEP 1: Register a few stations
        var station1 = Station.builder().stationName("Heart UK").build();
        var station2 = Station.builder().stationName("Capital FM").build();

        restTemplate.postForEntity(baseUrl, station1, Station.class);
        restTemplate.postForEntity(baseUrl, station2, Station.class);

        // Step 2: Make modifications on two separate instance
        var singleStationResponse = restTemplate.getForEntity(baseUrl.concat("/1"), Station.class);
        assertThat(singleStationResponse).isNotNull();
        var original = singleStationResponse.getBody();
        var forUser1 = Station.builder()
                .stationName(original.stationName() + " Modified ONE")
                .id(original.id())
                .version(original.version())
                .build();

        var forUser2 = Station.builder()
                .stationName(original.stationName() + " Modified TWO")
                .id(original.id())
                .version(original.version())
                .build();

        var amendResponse = restTemplate.exchange(baseUrl, HttpMethod.PUT, buildHttpEntity(forUser1), Station.class);
        assertThat(amendResponse).isNotNull();
        assertThat(amendResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {
            restTemplate.exchange(baseUrl, HttpMethod.PUT, buildHttpEntity(forUser2), Station.class);
            fail("This should throw a HttpClientErrorException.");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }
    }

    private HttpEntity<Station> buildHttpEntity(Station station) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(station, headers);
    }

    private void assertStatusCode(ResponseEntity<? extends Object> response, HttpStatus status) {
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(status);
    }

    private void assertStation(Station station, Long id, String name, Integer version) {
        assertThat(station).isNotNull();
        assertThat(station.id()).isEqualTo(id);
        assertThat(station.stationName()).isEqualTo(name);
        assertThat(station.version()).isEqualTo(version);
    }
}
