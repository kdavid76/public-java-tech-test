package com.global.aod.interview.techtest.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record Station(
        Long id,
        @NotBlank(message = "") String stationName,
        Integer version)
{ }
