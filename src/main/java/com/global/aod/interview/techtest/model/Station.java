package com.global.aod.interview.techtest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Schema(title = "Station", description = "Model for a station")
@Builder
public record Station(
        @Schema(description = "Station identifier.", example = "1")
        Long id,
        @Schema(description = "Name of Station", example = "Heart UK")
        @NotBlank(message = "") String stationName,
        @Schema(description = "Version of the Station resource", example = "0")
        Integer version) {
}
