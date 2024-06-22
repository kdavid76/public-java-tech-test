package com.global.aod.interview.techtest.mapper;

import com.global.aod.interview.techtest.model.Station;
import com.global.aod.interview.techtest.model.entity.StationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = ComponentModel.SPRING)
public interface StationMapper {
    StationMapper INSTANCE = Mappers.getMapper(StationMapper.class);

    @Mapping(target = "stationName", source = "name")
    Station toDto(StationEntity source);

    @Mapping(target = "name", source = "stationName")
    StationEntity toEntity(Station source);
}
