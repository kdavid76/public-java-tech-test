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
        summary = "Delete a selected a Station",
        description = "Delete a selected a Station",
        operationId = "deleteStation",
        tags = {"Stations"},
        responses = {
                @ApiResponse(responseCode = "204", description = "The station is successfully deleted.", content = {@Content(schema = @Schema())}),
                @ApiResponse(responseCode = "400", description = "The given Station identifier is not a number.", content = {@Content(schema = @Schema())}),
                @ApiResponse(responseCode = "500", description = "The background service layer failed to create the resource", content = {@Content(schema = @Schema())})
        }
)
public @interface DeleteStation {
}
