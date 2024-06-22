package com.global.aod.interview.techtest.contoller;

import com.global.aod.interview.techtest.model.Station;
import com.global.aod.interview.techtest.service.StationService;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationsController {

    private static final Logger log = LoggerFactory.getLogger(StationsController.class);
    private final StationService stationService;

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Station> crateStation(@Valid @RequestBody Station station, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("There are input validation failures.");
            // At this place we have the chance to calculate detailed result from the fields failed validation.
            // Maybe we should define a MessageSource for introducing multi-language, custom error messages
            // Without this part, the response is the standard Spring Boot BAQ_REQUEST response.
            return ResponseEntity.badRequest().build();
        }

        var response = stationService.createStation(station);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("")
    public ResponseEntity<List<Station>> findAllStations() {
        var listOfStations = stationService.findAllStations();

        if (CollectionUtils.isEmpty(listOfStations)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(listOfStations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> findById(@Nonnull @PathVariable Long id) {
        var station = stationService.findById(id);

        if (station == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(station);
    }
}
