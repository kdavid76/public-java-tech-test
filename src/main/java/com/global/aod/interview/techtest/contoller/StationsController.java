package com.global.aod.interview.techtest.contoller;

import com.global.aod.interview.techtest.annotations.CreateStation;
import com.global.aod.interview.techtest.annotations.DeleteStation;
import com.global.aod.interview.techtest.annotations.GetAllStations;
import com.global.aod.interview.techtest.annotations.GetStation;
import com.global.aod.interview.techtest.annotations.UpdateStation;
import com.global.aod.interview.techtest.model.Fields;
import com.global.aod.interview.techtest.model.Station;
import com.global.aod.interview.techtest.service.StationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Stations", description = "Station management APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/stations")
public class StationsController {

    private static final Logger log = LoggerFactory.getLogger(StationsController.class);
    private final StationService stationService;

    @CreateStation
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Station> crateStation(@Valid @RequestBody Station station, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("There are input validation failures while creating new station resource.");
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

    @GetAllStations
    @GetMapping("")
    public ResponseEntity<List<Station>> findAllStations(
            @RequestParam(name = "orderby", required = false) Fields field,
            @RequestParam(name = "direction", required = false) Sort.Direction direction) {

        var listOfStations = stationService.findAllStations(field, direction);

        if (CollectionUtils.isEmpty(listOfStations)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(listOfStations);
    }

    @GetStation
    @GetMapping("/{id}")
    public ResponseEntity<Station> findById(@Nonnull @PathVariable Long id) {
        var station = stationService.findById(id);

        if (station == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(station);
    }

    @UpdateStation
    @PutMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Station> updateStation(@Valid @RequestBody Station station, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("There are input validation failures while updating station resource");
            // At this place we have the chance to calculate detailed result from the fields failed validation.
            // Maybe we should define a MessageSource for introducing multi-language, custom error messages
            // Without this part, the response is the standard Spring Boot BAQ_REQUEST response.
            return ResponseEntity.badRequest().build();
        }

        var response = stationService.updateStation(station);
        return ResponseEntity.ok(response);
    }

    @DeleteStation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
