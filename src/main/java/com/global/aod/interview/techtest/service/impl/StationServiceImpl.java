package com.global.aod.interview.techtest.service.impl;

import com.global.aod.interview.techtest.mapper.StationMapper;
import com.global.aod.interview.techtest.model.Fields;
import com.global.aod.interview.techtest.model.Station;
import com.global.aod.interview.techtest.repository.StationRepository;
import com.global.aod.interview.techtest.service.StationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Sort;
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
        var responseEntity = repository.save(entity);

        return mapper.toDto(responseEntity);
    }

    @Override
    public List<Station> findAllStations(Fields field, Sort.Direction direction) {
        log.info("Getting all Stations...");
        return repository.findAll(
                Sort.by(direction != null ? direction : Sort.Direction.ASC,
                        field != null ? field.getField() : Fields.ID.getField())
        ).stream().map(mapper::toDto).toList();

    }

    @Override
    public Station findById(Long id) {
        log.info("Getting station data with id={}", id);
        var entity = repository.findById(id);
        return mapper.toDto(entity.orElse(null));
    }

    @Override
    @Transactional
    public Station updateStation(Station station) {
        /*
        This method is only called with validated input. So, a valid stationName is guaranteed.
        If the automatic bean validation is not an option anymore, then we need to test/validate the input here.
         */
        log.info("Updating station with data={}", station);

        var entity = mapper.toEntity(station);

        try {
            var responseEntity = repository.saveAndFlush(entity);
            return mapper.toDto(responseEntity);
        } catch (OptimisticLockingFailureException e) {
            log.error("Version mismatch while updating station.", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "", e);
        }
    }

    @Override
    public void deleteStation(Long id) {
        log.info("Deleting station with id={}", id);
        repository.deleteById(id);
    }
}
