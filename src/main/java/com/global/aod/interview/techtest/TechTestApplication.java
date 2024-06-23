package com.global.aod.interview.techtest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Stations microservice"))
@SpringBootApplication
public class TechTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechTestApplication.class, args);
    }
}
