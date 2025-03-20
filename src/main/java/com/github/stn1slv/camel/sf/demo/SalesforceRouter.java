package com.github.stn1slv.camel.sf.demo;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

/**
 * A Camel router class that integrates with Salesforce to manage contact information.
 * This router implements five main routes:
 * 1. REST GET endpoint to fetch all contacts
 * 2. REST GET endpoint to retrieve a specific contact by ID
 * 3. REST PUT endpoint to update a specific contact by ID
 * 4. Salesforce CDC event listener for Contact changes
 * 5. Scheduled timer-based route for periodic contact fetching
 *
 * Key features:
 * - REST API: Servlet-based REST endpoints with JSON binding
 * - CRUD Operations: Support for reading and updating Salesforce contacts
 * - Real-time Updates: CDC (Change Data Capture) event monitoring
 * - Scheduled Tasks: Automated periodic contact synchronization
 *
 * Endpoints:
 * - GET /contacts: Retrieves all contacts
 * - GET /contacts/{id}: Retrieves a specific contact
 * - PUT /contacts/{id}: Updates a specific contact
 *
 * Technical details:
 * - Uses Spring @Component for dependency injection
 * - Implements RestBindingMode.json for automatic JSON serialization
 * - Leverages direct endpoints for synchronous route execution
 * - Integrates with Salesforce using SOQL queries and CDC events
 * - Employs Jackson for JSON data transformation
 *
 * @see org.apache.camel.builder.RouteBuilder
 * @see org.springframework.stereotype.Component
 */
@Component
public class SalesforceRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Configure REST endpoint using servlet component and JSON binding
        restConfiguration()
            .component("servlet")        // Use servlet as the HTTP server
            .bindingMode(RestBindingMode.json);      // Enable automatic JSON data binding

        // Define REST endpoint that responds to GET requests
        rest("/contacts")
            .get()
                .id("Rest-based route: all contacts")       // Create GET endpoint at /contacts path
                .to("direct:getContacts?synchronous=true") // Route requests to direct:getContacts endpoint
            .get("/{id}")
                .id("Rest-based route: contact by id")          // Create GET endpoint with path parameter
                .to("direct:getContactById?synchronous=true") // Route requests to direct:getContactById endpoint
            .put("/{id}")
                .id("Rest-based route: update contact by id")  // Create GET endpoint with path parameter
                .to("direct:updateContactById?synchronous=true"); // Route requests to direct:updateContactById endpoint
        
        // Define route that queries Salesforce contacts
        from("direct:getContacts")
            // Execute SOQL query to get Contact objects from Salesforce
            .to("salesforce:queryAll?sObjectQuery=SELECT Id, Name, Email FROM Contact")
            // Uncommented debug logging line
            // .to("log:debug?showAll=true&multiline=true")
            // Convert Salesforce response to JSON using Jackson library
            .unmarshal().json(JsonLibrary.Jackson);

        // Define route that queries Salesforce contacts
        from("direct:getContactById")
            // Execute SOQL query to get Contact objects from Salesforce
            .toD("salesforce:getSObject?sObjectName=Contact&sObjectId=${header.id}")
            // Uncommented debug logging line
            // .to("log:debug?showAll=true&multiline=true");
            // Convert Salesforce response to JSON using Jackson library
            .unmarshal().json(JsonLibrary.Jackson);
        
        // Define route that updates a Salesforce contact by ID
        from("direct:updateContactById")
            // Convert the input body to JSON format using Jackson library
            .marshal().json(JsonLibrary.Jackson)
            // Convert the JSON to String format for Salesforce update
            .convertBodyTo(String.class)
            // Uncommented debug logging line for troubleshooting
            // .to("log:debug?showAll=true&multiline=true")
            // Update the Contact object in Salesforce using the ID from the header
            .toD("salesforce:updateSObject?sObjectName=Contact&sObjectId=${header.id}");

        // Define route that listens for Salesforce CDC events for Contact objects
        from("salesforce:subscribe:data/ContactChangeEvent")
            .id("Listener Salesforce CDC events") // Set route ID for monitoring
            // Uncommented debug logging line
            // .to("log:debug?showAll=true&multiline=true");
            // Convert Salesforce response to JSON using Jackson library
            .unmarshal().json(JsonLibrary.Jackson)
            // Log the CDC event at INFO level
            .log(LoggingLevel.INFO, "A new event: ${body}"); 
        
        // Define timer-based route that runs every 60 seconds
        from("timer:fire?period=60000")                                     // Create timer trigger
            .id("Scheduler-based route: all contacts")                       // Set route ID for monitoring
            .to("direct:getContacts?synchronous=true")                      // Call the same contacts query route
            .log(LoggingLevel.INFO, "Salesforce response: ${body}");    // Log the response
    }
}
