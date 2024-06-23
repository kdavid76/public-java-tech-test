package com.global.aod.interview.techtest.service;

import com.global.aod.interview.techtest.model.Fields;
import com.global.aod.interview.techtest.model.Station;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface StationService {

    Station createStation(Station station);

    List<Station> findAllStations(Fields fields, Sort.Direction direction);

    Station findById(Long id);

    Station updateStation(Station station);

    void deleteStation(Long id);
}
