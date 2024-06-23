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
        summary = "Update an existing Station resource",
        description = "Update an existing Station resource",
        operationId = "updateStation",
        tags = {"Stations"},
        responses = {
                @ApiResponse(responseCode = "200", description = "Station resource successfully updated (or created).",
                        content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Station.class))}),
                @ApiResponse(responseCode = "409", description = "The selected station has already been modified by someone another user", content = {@Content(schema = @Schema())}),
                @ApiResponse(responseCode = "500", description = "The background service layer failed to create the resource", content = {@Content(schema = @Schema())})
        }
)
public @interface UpdateStation {
}
