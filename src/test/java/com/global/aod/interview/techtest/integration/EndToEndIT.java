package com.global.aod.interview.techtest.integration;

import com.global.aod.interview.techtest.model.Station;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class EndToEndIT {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private String baseUrl = "http://localhost";


    @BeforeEach
    public void setUp() {

        baseUrl = baseUrl.concat(":").concat(port + "").concat("/stations");
    }

    @Sql(scripts = "/db/reset-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @DisplayName("End to end test with creating, retrieving, amending and deleting station data")
    void shouldWorkEndToEnd() {
        // STEP 1: Retrieve the list of stations, but there's nothing in the database
        var stationListResponse = restTemplate.getForEntity(baseUrl, Station[].class);
        assertThat(stationListResponse.getStatusCode()).isNotNull().isEqualTo(HttpStatus.NO_CONTENT);

        // STEP 2: Register a few stations
        addAndCheckFourStations(baseUrl);

        // STEP 3: Retrieve the list of stations for checking the number of available ones
        stationListResponse = restTemplate.getForEntity(baseUrl, Station[].class);
        assertStatusCode(stationListResponse, HttpStatus.OK);
        assertThat(stationListResponse.getBody()).isNotNull().hasSize(4);

        // STEP 4: Get only one selected Station
        var singleStationResponse = restTemplate.getForEntity(baseUrl.concat("/2"), Station.class);
        assertStatusCode(singleStationResponse, HttpStatus.OK);
        assertStation(singleStationResponse.getBody(), 2L, "Capital FM", 0);

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

    @Sql(scripts = "/db/reset-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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

    @Sql(scripts = "/db/reset-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @DisplayName("Testing ordering of findAllStations call")
    void shouldOrderByTheGivenParameters() {
        // STEP 1: Add stations
        addAndCheckFourStations(baseUrl);

        // STEP 2: Retrieve the list of stations orderby=nAMe, direction default (ASC)
        var stationListResponse = restTemplate.getForEntity(baseUrl + "?orderby=nAMe", Station[].class);
        assertStatusCode(stationListResponse, HttpStatus.OK);
        var body = stationListResponse.getBody();
        assertThat(body).isNotNull();
        assertStationArrayOrderByName(body, "Capital FM", "Heart UK", "Kiss", "Radio X");

        // STEP 3: Retrieve the list of stations orderby=id, direction default (ASC)
        stationListResponse = restTemplate.getForEntity(baseUrl + "?orderby=id", Station[].class);
        assertStatusCode(stationListResponse, HttpStatus.OK);
        body = stationListResponse.getBody();
        assertThat(body).isNotNull();
        assertStationArrayOrderByName(body, "Heart UK", "Capital FM", "Radio X", "Kiss");

        // STEP 4: Retrieve the list of stations orderby=nAMe, direction=DESC
        stationListResponse = restTemplate.getForEntity(baseUrl + "?orderby=nAMe&direction=desc", Station[].class);
        assertStatusCode(stationListResponse, HttpStatus.OK);
        body = stationListResponse.getBody();
        assertThat(body).isNotNull();
        assertStationArrayOrderByName(body, "Radio X", "Kiss", "Heart UK", "Capital FM");

        // STEP 5: Retrieve the list of stations orderby=id, direction=desc
        stationListResponse = restTemplate.getForEntity(baseUrl + "?orderby=id&direction=desc", Station[].class);
        assertStatusCode(stationListResponse, HttpStatus.OK);
        body = stationListResponse.getBody();
        assertThat(body).isNotNull();
        assertStationArrayOrderByName(body, "Kiss", "Radio X", "Capital FM", "Heart UK");
    }

    private HttpEntity<Station> buildHttpEntity(Station station) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return new HttpEntity<>(station, headers);
    }

    private void assertStationArrayOrderByName(Station[] stations, String... names) {
        for (int i = 0; i < stations.length; i++) {
            assertThat(stations[i]).isNotNull();
            assertThat(stations[i].stationName()).isEqualTo(names[i]);
        }
    }

    protected static void assertStatusCode(ResponseEntity<? extends Object> response, HttpStatus status) {
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(status);
    }

    protected static void assertStation(Station station, Long id, String name, Integer version) {
        assertThat(station).isNotNull();
        assertThat(station.id()).isEqualTo(id);
        assertThat(station.stationName()).isEqualTo(name);
        assertThat(station.version()).isEqualTo(version);
    }

    private void addAndCheckFourStations(String baseUrl) {
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
    }
}
