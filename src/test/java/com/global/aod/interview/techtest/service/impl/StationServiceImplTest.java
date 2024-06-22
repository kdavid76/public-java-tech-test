package com.global.aod.interview.techtest.service.impl;

import com.global.aod.interview.techtest.mapper.StationMapper;
import com.global.aod.interview.techtest.model.Station;
import com.global.aod.interview.techtest.model.entity.StationEntity;
import com.global.aod.interview.techtest.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationServiceImplTest {

    private static final String STATION_NAME = "Heart FM";

    @InjectMocks
    private StationServiceImpl impl;

    @Mock
    private StationRepository mockRepository;

    @Mock
    private StationMapper mockMapper;

    @BeforeEach
    public void setup() {
        reset(mockMapper, mockRepository);
    }

    /*
    @Test
    @DisplayName("createStation - Throw ResponseStatusException when cannot save station")
    void shouldThrowExceptionWhenPersistingFails() {
        var station = Station.builder().stationName(STATION_NAME).build();
        var stationEntity = new StationEntity(1L, STATION_NAME, 0);

        when(mockMapper.toEntity(station)).thenReturn(stationEntity);
        when(mockRepository.save(stationEntity)).thenThrow(new IllegalArgumentException("There is trouble here!"));

        assertThatThrownBy(() -> impl.createStation(station))
                .isInstanceOf(ResponseStatusException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class);

        verifyNoMoreInteractions(mockMapper);
    }
     */

    @Test
    @DisplayName("createStation - Station successfully saved.")
    void shouldSuccessfullySaveStation() {
        var station = Station.builder().stationName(STATION_NAME).build();
        var stationEntity = new StationEntity(1L, STATION_NAME, 0);
        var responseStation = Station.builder().stationName(STATION_NAME).id(1L).version(0).build();

        when(mockMapper.toEntity(station)).thenReturn(stationEntity);
        when(mockRepository.save(stationEntity)).thenReturn(stationEntity);
        when(mockMapper.toDto(stationEntity)).thenReturn(responseStation);

        var response = impl.createStation(station);
        assertThat(response).isNotNull().isEqualTo(responseStation);
    }

    @Test
    @DisplayName("findAllStations - Getting all stations")
    void shouldReturnAllStations() {
        var station = Station.builder().stationName(STATION_NAME).id(1L).version(0).build();
        var stationEntity = new StationEntity(1L, STATION_NAME, 0);

        when(mockRepository.findAll()).thenReturn(List.of(stationEntity, stationEntity, stationEntity));
        when(mockMapper.toDto(stationEntity)).thenReturn(station);

        var response = impl.findAllStations();
        assertThat(response).isNotNull().hasSize(3);
    }

    @Test
    @DisplayName("findById - The station waas found")
    void shouldReturnStation() {
        var station = Station.builder().stationName(STATION_NAME).id(1L).version(0).build();
        var stationEntity = new StationEntity(1L, STATION_NAME, 0);

        when(mockRepository.findById(1L)).thenReturn(Optional.of(stationEntity));
        when(mockMapper.toDto(stationEntity)).thenReturn(station);

        var response = impl.findById(1L);
        assertThat(response).isNotNull().isEqualTo(station);
    }

    /*
    @Test
    @DisplayName("updateStation - Throw ResponseStatusException when cannot save station")
    void shouldThrowExceptionWhenUpdateFails() {
        var station = Station.builder().stationName(STATION_NAME).build();
        var stationEntity = new StationEntity(1L, STATION_NAME, 0);

        when(mockMapper.toEntity(station)).thenReturn(stationEntity);
        when(mockRepository.save(stationEntity)).thenThrow(new IllegalArgumentException("There is trouble here!"));

        assertThatThrownBy(() -> impl.updateStation(station))
                .isInstanceOf(ResponseStatusException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class);

        verifyNoMoreInteractions(mockMapper);
    }
     */

    @Test
    @DisplayName("updateStation - Optimistic locking failure when cannot save station")
    void shouldThrowOptimisticLockingFailureWhenUpdateFails() {
        var station = Station.builder().stationName(STATION_NAME).build();
        var stationEntity = new StationEntity(1L, STATION_NAME, 0);

        when(mockMapper.toEntity(station)).thenReturn(stationEntity);
        when(mockRepository.save(stationEntity)).thenThrow(new OptimisticLockingFailureException("There is trouble here!"));

        assertThatThrownBy(() -> impl.updateStation(station))
                .isInstanceOf(ResponseStatusException.class)
                .hasCauseInstanceOf(OptimisticLockingFailureException.class);

        verifyNoMoreInteractions(mockMapper);
    }

    @Test
    @DisplayName("updateStation - Station successfully saved.")
    void shouldSuccessfullyUpdateStation() {
        var station = Station.builder().stationName(STATION_NAME).build();
        var stationEntity = new StationEntity(1L, STATION_NAME, 0);
        var responseStation = Station.builder().stationName(STATION_NAME).id(1L).version(0).build();

        when(mockMapper.toEntity(station)).thenReturn(stationEntity);
        when(mockRepository.save(stationEntity)).thenReturn(stationEntity);
        when(mockMapper.toDto(stationEntity)).thenReturn(responseStation);

        var response = impl.updateStation(station);
        assertThat(response).isNotNull().isEqualTo(responseStation);
    }

    @Test
    @DisplayName("deleteStation - Station deleted.")
    void shouldDeleteStation() {
        impl.deleteStation(1L);
        verify(mockRepository).deleteById(1L);
    }
}
