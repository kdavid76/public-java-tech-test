package com.global.aod.interview.techtest.mapper;

import com.global.aod.interview.techtest.model.Station;
import com.global.aod.interview.techtest.model.entity.StationEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StationMapperTest {

    private final static StationMapper mapper = StationMapper.INSTANCE;

    @Test
    @DisplayName("ToDto - Should return null when input is null")
    void shouldReturnNullDto() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    @DisplayName("ToDto - Dto mapped")
    void shouldMapDto() {
        var entity = new StationEntity(1L, "Name of Station", 1);
        var dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(entity.getId());
        assertThat(dto.stationName()).isEqualTo(entity.getName());
        assertThat(dto.version()).isEqualTo(entity.getVersion());
    }

    @Test
    @DisplayName("ToEntity - Should return null when input is null")
    void shouldReturnNullEntity() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("ToEntity - Entity mapped")
    void shouldMapEntity() {
        var dto = Station.builder().stationName("Name of Station").version(1).id(1L).build();
        var entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.id());
        assertThat(entity.getName()).isEqualTo(dto.stationName());
        assertThat(entity.getVersion()).isEqualTo(dto.version());
    }
}
