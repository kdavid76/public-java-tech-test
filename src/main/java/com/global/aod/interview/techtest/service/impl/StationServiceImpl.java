package com.global.aod.interview.techtest.service.impl;

import com.global.aod.interview.techtest.mapper.StationMapper;
import com.global.aod.interview.techtest.model.Station;
import com.global.aod.interview.techtest.repository.StationRepository;
import com.global.aod.interview.techtest.service.StationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationServiceImpl implements StationService {

    private final StationRepository repository;
    private final StationMapper mapper;

    @Override
    @Transactional
    public Station createStation(Station station) {
        /*
        This method is only called with validated input. So, a valid stationName is guaranteed.
        If the automatic bean validation is not an option anymore, then we need to test/validate the input here.
         */
        log.info("Creating station name={}", station.stationName());

        var entity = mapper.toEntity(station);

        try {
            var responseEntity = repository.save(entity);
            return mapper.toDto(responseEntity);
        } catch (Exception e) {
            log.error("Unexpected error while persisting Station to database.", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }
    }

    @Override
    public List<Station> findAllStations() {
        log.info("Getting all Stations...");
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public Station findById(Long id) {
        log.info("Getting station data with id={}", id);
        var entity = repository.findById(id);
        return mapper.toDto(entity.orElse(null));
    }
}
