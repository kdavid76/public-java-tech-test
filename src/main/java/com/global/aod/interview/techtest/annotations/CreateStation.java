package com.global.aod.interview.techtest.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Operation(
        summary = "Create a new Station resource",
        description = "Creates a new Station resource",
        operationId = "createStation",
        tags = {"Stations"},
        responses = {
                @ApiResponse(responseCode = "201", description = "Station resource successfully created.", content = {@Content(schema = @Schema())}),
                @ApiResponse(responseCode = "400", description = "Invalid input, stationName is missing."),
                @ApiResponse(responseCode = "500", description = "The background service layer failed to create the resource", content = {@Content(schema = @Schema())})
        }
)
public @interface CreateStation {
}
