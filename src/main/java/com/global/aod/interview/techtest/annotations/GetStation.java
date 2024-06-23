package com.global.aod.interview.techtest.annotations;

import com.global.aod.interview.techtest.model.Station;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Operation(
        summary = "Get the data of a selected a Station",
        description = "Retrieve the data of a selected a Station",
        operationId = "getStation",
        tags = {"Stations"},
        responses = {
                @ApiResponse(responseCode = "200", description = "Station resource successfully created.",
                        content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Station.class))}),
                @ApiResponse(responseCode = "400", description = "The given Station identifier is not a number.", content = {@Content(schema = @Schema())}),
                @ApiResponse(responseCode = "404", description = "Station with the given identifier can't be found.", content = {@Content(schema = @Schema())}),
                @ApiResponse(responseCode = "500", description = "The background service layer failed to create the resource", content = {@Content(schema = @Schema())})
        }
)
public @interface GetStation {
}
