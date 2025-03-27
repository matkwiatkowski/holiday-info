#  Holiday Information Service

This service is providing API for getting

## Installing / Getting started

### Requirements

For running the application `java 21` and `Docker` with `docker compose` is required.

### Building and running the application

You can also use globally available maven for building:
```shell
mvn clean install
```
or use maven wrapper:
```shell
./mvnw clean install
```

You can use prepared script for building and running application locally. Simply run:
```shell
./run-local.sh -b
```
This will build the application and run docker compose stack.

Run `./run-local -h` for more information.

### Initial Configuration

In `.env` there is the initial configuration that can be used for local testing.
One this missing is `HOLIDAY_API_KEY` which is required to run the service. 
You can get your key on [Holiday API](https://holidayapi.com/) site.

### Testing

When docker compose stack is up, the application should be available under port 8080 by default.
There is a single endpoint `GET /api/holidays/upcoming` that can be used for testing.
Both `countryCode` and `from` query parameters (`countryCode` can be specified multiple times).
Example request `http://localhost:8080/api/holidays/upcoming?from=2024-01-10&countryCode=US&countryCode=DE`.

## Design decisions:

Found 2 widely used solutions to get the holiday info from:
- [Holiday API](https://holidayapi.com/)
- [Jollyday](https://jollyday.sourceforge.net/)

Both have some disadvantages:
- Holiday API is online only solution,
- Jollyday can have some days missing, less options for filtering.

Decided to give a configuration option to switch between the two.

Since the only performance issue might be large number of requests to Holiday API, application is designed to get 
holidays for the whole year and work on that.

Decided to drop option to filter by public/non-public holidays since Jollyday does not have this option.

NOTE: as of right now only Holiyday API support is implemented.

## Next steps/TODO

- Add option to add api-key through API, maybe admin login?
- Generate token based on api-key?
- CI/CD
- Add Swagger (configurable)
- Add ssl (through nginx or directly in spring)
- Configure logging and log rotation
- Sonarqube
- Test different encodings
- Add Jollyday as second holiday info source
- Docker security (non-root user, limit processes, etc.)
- Security scans
- Internalization (for errors mostly)
