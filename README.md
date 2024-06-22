# Global AOD Java Tech Test

## Technology choices
The definition of this task is lining out what sort of tech shall I use here. It's a Spring Boot based application which should be using using Spring Data JPA or JDBC modules for handling database operations. I go with Spring Data Jpa because, it provide easier options to build solutions working with databases, and I have way more experience with that compared to how much I've worked with Spring Data JDBC.

Let's imagine this is a real life system. I don't think it will be under heavy load, maybe getting station parameters might is the only feature of this service, which will be used considerably more than the others. But I don't think the anticipated level of traffic requires reactive, block free approach here. There are no complicated and time consuming queries or I/O operations in the background, there is no large amount of data processing involved. This is why I suggest and I will use the traditional Spring Web approach.

I neither will go into details of different security aspects, because - I suppose - that is out of the scope of this task. However, if you continue reading, you'll find suggestions how some operations can be secured.

Logging might not be the first thing to handle when you build a system like this, but it is important. For the sake of simplicity, I won't setup any complicated log file rolling strategies, I just use the console logging with the help of a library called `Lombok`. That will also help us eliminating some nasty boilerplate for database entity classes. 

## Considerations and assumptions
This seems to be a simple CRUD application. The main features are Creating, Reading, Updating and Deleting simple records of radio stations.

### Error handling
There are various ways how errors can be handled in a RESTful application. Without getting deeper into different techniques, I will use a very simple and straight forward way of handling errors. Whenever the application faces some exceptional situation, the system just throws a `ResponseStatusException` with the appropriate `HttpStatus`. The Spring Framework will automatically set the status of the *http response*, but there is a trade-off here. We have to give up on custom response objects, we always have to use the response structure defined by Spring Framework.

As an improvement to the system we may be want to upgrade this and use some more sophisticated approach for sending customised success and failure information to API clients.

### Creating station
Stations can be created by sending a `POST` request to the URI: `/station` with a `JSON` payload containing the name of the station. Example:
```
{
  "stationName": "Heart FM"
}
```
The station identifier will be generated automatically upon saving the object to Postgres database. The `stationName` parameter must be provided. Its existence is checked by the Spring Framework, thanks to validation annotations. I give a non comprehensive example on how to handel these validation errors, and how to send back meaningful error messages to our clients.

In a real life environment, it might be important who can create a new station resource in the database. This aspect is not part of the requirements of this task, so I don't implement anything just now. But a few options are available for us, when we are thinking about the security of our services. As per this is a Spring Boot based application, Spring Security is kind of a natural choice for authorisation.

### Reading station data

### Reading a list of stations

In a real/live environment, the parameters of a radio station aren't changing really frequently. But when they are changing we need to make sure only one client can write it at the same time. For demonstrating this, a very simple optimistic locking scenario is implemented. And I've added a new row to the database table 

, it must be  and only one process can change that.  it is possibly read quite often. So for this example and the given database structure it does not require  


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
