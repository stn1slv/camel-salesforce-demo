# Camel Salesforce Demo

A Spring Boot application demonstrating integration with Salesforce using Apache Camel. This demo shows how to query Salesforce contacts using both REST endpoints and scheduled jobs.

## Features

- REST API endpoints to fetch all Salesforce contacts and get contact by ID
- Scheduled job that periodically retrieves contacts
- Spring Boot and Apache Camel integration
- Salesforce authentication using client credentials flow

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Salesforce developer account
- Salesforce Connected App credentials

## Configuration

1. Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties`
2. Update the following properties with your Salesforce credentials:
```properties
camel.component.salesforce.client-id=<YOUR_CLIENT_ID>
camel.component.salesforce.client-secret=<YOUR_CLIENT_SECRET>
camel.component.salesforce.instance-url=<YOUR_DOMAIN>
camel.component.salesforce.login-url=<YOUR_DOMAIN>
```

## Building

```bash
mvn clean install
```

## Running

```bash
mvn spring-boot:run
```

The application will start on port 8080.

## Testing

### REST Endpoint
To fetch all contacts via the REST endpoint:
```bash
curl http://localhost:8080/camel/contacts
```
To fetch a specific contact by ID:
```bash
curl http://localhost:8080/camel/contacts/{id}
```
Replace `{id}` with the actual Salesforce Contact ID.

### Scheduled Job
The application automatically queries Salesforce contacts every 10 seconds and logs the results.

## Project Structure

- `SalesforceRouter.java`: Contains Camel route definitions
- `App.java`: Spring Boot application entry point
- `application.properties`: Configuration properties

## Dependencies

- Spring Boot 3.4.3
- Apache Camel 4.10.2
- Camel Salesforce Component
- Camel Spring Boot Components

## License

MIT License - see LICENSE file for details