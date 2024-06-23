# Global AOD Java Tech Test

## Technology choices

The definition of this task is lining out what sort of tech shall I use here. It's a Spring Boot based application which
should be using using Spring Data JPA or JDBC modules for handling database operations. I go with Spring Data Jpa
because, it provide easier options to build solutions working with databases, and I have way more experience with that
compared to how much I've worked with Spring Data JDBC.

Let's imagine this is a real life system. I don't think it will be under heavy load, maybe getting station parameters
might is the only feature of this service, which will be used considerably more than the others. But I don't think the
anticipated level of traffic requires reactive, block free approach here. There are no complicated and time consuming
queries or I/O operations in the background, there is no large amount of data processing involved. This is why I suggest
and I will use the traditional Spring Web approach.

I neither will go into details of different security aspects, because - I suppose - that is out of the scope of this
task. However, if you continue reading, you'll find suggestions how some operations can be secured.

Logging might not be the first thing to handle when you build a system like this, but it is important. For the sake of
simplicity, I won't setup any complicated log file rolling strategies, I just use the console logging with the help of a
library called `Lombok`. That will also help us eliminating some nasty boilerplate for database entity classes.

## Considerations and assumptions

This seems to be a simple CRUD application. The main features are Creating, Reading, Updating and Deleting simple
records of radio stations.

### Error handling

There are various ways how errors can be handled in a RESTful application. Without getting deeper into different
techniques, I will use a very simple and straight forward way of handling errors. Whenever the application faces some
exceptional situation, the system just throws a `ResponseStatusException` with the appropriate `HttpStatus`. The Spring
Framework will automatically set the status of the *http response*, but there is a trade-off here. We have to give up on
custom response objects, we always have to use the response structure defined by Spring Framework.

As an improvement to the system we may be want to upgrade this and use some more sophisticated approach for sending
customised success and failure information to API clients.

### Testing

Meaningful unit test cases has been added added to the code. They test all the scenarios what can happen in the code. On
the top of that, there are a two integration test cases. The first one tests the whole flow, of creating, reading and
amending resources with checking for erroneous scenarios as well. The second one demonstrates, how the optimistic
locking feature works.

### Health indicator

This application uses the `/actuator/health` endpoint for checking the health status of the system. The main component
is the database, and the Spring Boot initiates the health checking for us. By calling the mentioned endpoint, the
response will be something similar:

```
{
    "status": "UP",
    "components": {
        "db": {
            "status": "UP",
            "details": {
                "database": "PostgreSQL",
                "validationQuery": "isValid()"
            }
        },
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 494384795648,
                "free": 233683619840,
                "threshold": 10485760,
                "path": "/Users/davidkrisztian/git/public-java-tech-test/.",
                "exists": true
            }
        },
        "ping": {
            "status": "UP"
        }
    }
}
```

Which shows the most important part, the health status of the database.

### API Documentation

API documentation is generated automatically. It is available at `/swagger-ui.html`

### API description

#### Creating station

Stations can be created by sending a `POST` request to the URI: `/stations` with a `JSON` payload containing the name of
the station. Example:

```
{
  "stationName": "Heart FM"
}
```

The station identifier will be generated automatically upon saving the object to Postgres database. The `stationName`
parameter is mandatory. Its existence is checked by the Spring Framework, thanks to validation annotations. I give a
non comprehensive example on how to handle these validation errors, and send back meaningful error messages to
our clients.

The successful response will have the status `CREATED(401)` and will have a header called `Location` and its value will
be the URL of the newly created Station
resource. The endpoint also sends back the newly created resource, just for an extra piece of mind:

```
{
    "id": 6,
    "stationName": "Heart UK",
    "version": 0
}
```

When saving the resource fails, the response status will be `INTERNAL_SERVER_ERROR(500)`. When the
mandatory `stationName` is not provided in the request payload, the response will have http status `BAD_REQUEST(400)`.

In a real life environment, it might be important who can create a new station resource in the database. This aspect is
not part of the requirements of this task, so I don't implement anything just now. But a few options are available for
us, when we are thinking about the security of our services. As per this is a Spring Boot based application, Spring
Security is kind of a natural choice for authorisation.

#### Retrieving the list of all stations

The list of all stations can be retrieved by sending a `GET` request to the URI: `/stations`. The successful response
will look like this, and will have http status `OK(200)`:

```
[
    {
        "id": 2,
        "stationName": "Heart UK",
        "version": 0
    },
    {
        "id": 3,
        "stationName": "Heart UK",
        "version": 0
    },
    {
        "id": 4,
        "stationName": "Heart UK",
        "version": 0
    }
]
```

When the background service fails, then `INTERNAL_SERVER_ERROR(500)` will be returned.

If you call this endpoint before any station data is persisted, then the response still will be `OK(200)`
and the payload will be empty.

#### Retrieving data for a selected station

The data of a selected station can be retrieved by sending a `GET` request to `/stations/{id}` where the `{id}` part is
the identifier of the station. If it can be found then it will be returned alongside an http `OK(200)` response in this
format:

```
{
    "id": 6,
    "stationName": "Heart UK",
    "version": 0
}
```

If the background service fails http `INTERNAL_SERVER_ERROR(500)` will be returned. The station identifier is a `Long`.
If you try it with something what is not a number, then http status `BAD_REQUEST(400)` will be the result. When the
Station can't be found, the response will be `NOT_FOUND(404)`

#### Modify station data

Amending station data is very similar to creating new stations. A `PUT` request needs to be sent to `/stations`
endpoint with the payload of

```
{
    "id": 6,
    "stationName": "Capital London",
    "version": 0
}
```

In case of successful modification of the data, http status `OK(200)` will be returned. If the backed service fails
http `INTERNAL_SERVER_ERROR(500)`. If the station with the given identifier does not exists, it will be created. Every
time when the operation is successful, the response will have a payload of:

```
{
    "id": 6,
    "stationName": "Capital London",
    "version": 1
}
```

Which shows that the attribute version is incremented by the framework automatically. In a real/live environment, the
parameters of a radio station aren't changing really frequently. But when they are
changing we need to make sure only one client can write it at the same time. For demonstrating this, a very simple
optimistic locking scenario is implemented. And that's why I have added the `version` column to the database.
In this unlikely scenario the response will be http status `CONFLICT(409)`

#### Delete a selected station

A selected station can be deleted by sending a `DELETE` request to `/stations/{id}` where the `{id}` part is
the identifier of the station. If the backend service finishes in an exceptional scenario, the response will
be `INTERNA_SERVER_ERROR(500)`, otherwise the response is always `NO_CONTENT(204)`.

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
