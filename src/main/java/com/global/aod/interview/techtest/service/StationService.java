package com.global.aod.interview.techtest.service;

import com.global.aod.interview.techtest.model.Station;

import java.util.List;

public interface StationService {

    Station createStation(Station station);

    List<Station> findAllStations();

    Station findById(Long id);
}
