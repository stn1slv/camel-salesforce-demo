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

1. Create a Connected App in your Salesforce org:
   - Go to Setup > Apps > App Manager > New Connected App
   - Enable OAuth Settings
   - Set Callback URL (can be http://localhost:8080)
   - Add 'Manage user data via APIs' to Selected OAuth Scopes
   - Save and wait for activation

2. Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties`

3. Update the properties with your Connected App credentials:
```properties
camel.component.salesforce.client-id=<YOUR_CLIENT_ID>         # Consumer Key from Connected App
camel.component.salesforce.client-secret=<YOUR_CLIENT_SECRET> # Consumer Secret from Connected App
camel.component.salesforce.instance-url=<YOUR_DOMAIN>         # e.g. https://your-org.my.salesforce.com
camel.component.salesforce.login-url=<YOUR_DOMAIN>           # Same as instance-url
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

### REST Endpoints

1. Fetch all contacts:
```bash
curl -X GET http://localhost:8080/camel/contacts | jq
```

2. Fetch a specific contact:
```bash
curl -X GET http://localhost:8080/camel/contacts/003XXXXXXXXXXXXXXX | jq
```
Replace `003XXXXXXXXXXXXXXX` with an actual Salesforce Contact ID.

3. Monitor CDC events:
   - Make changes to contacts in Salesforce
   - Watch the application logs for real-time change events

### Scheduled Job
The application runs two automated processes:
- Queries all contacts every 60 seconds
- Listens continuously for Contact Change Events (CDC)

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