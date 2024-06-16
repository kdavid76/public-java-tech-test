# Global AOD Java Tech Test

## Overview

This is a Java Spring Boot application which is intended to be extended with new functionality.

It contains the following features:

- Spring Boot Web for http endpoints.
- Spring Boot Data JDBC or JPA for database access.
- Flyway migrations.
- A Postgres database.

## Testing the application

- Ensure docker is running.
- Start the database.
- `docker compose up -d postgres`
- Run the tests with gradle (`gradle test`) or in the IDE.

## Running the application

- Ensure Docker is running
  `docker compose up -d postgres`
  `gradle bootRun`

## Database

The database is a Postgres 16 database.

Its connection details can be found in [application.yml](src/main/resources/application.yml)
Its schema is located at `src/main/resources/db/migration`
It can be started via docker compose.

# Complete the following tasks.

1. Create endpoints to allow for CRUD actions on Station
2. Provide an endpoint to return a list of stations, and the ability to sort by name or ID
3. Include some of the key tests you feel are necessary
4. Include a health check endpoint for the service
