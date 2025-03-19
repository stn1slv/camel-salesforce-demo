package com.github.stn1slv.camel.sf.demo;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

/**
 * A Camel router class that integrates with Salesforce to fetch contact information.
 * This router sets up four main routes:
 * 1. A REST endpoint to get all contacts via HTTP GET request
 * 2. A REST endpoint to get a specific contact by ID
 * 3. A Salesforce CDC event listener for Contact changes
 * 4. A scheduled timer-based route that periodically fetches all contacts
 *
 * Key components:
 * - REST configuration: Sets up a servlet-based REST endpoint with JSON binding
 * - Salesforce queries: Retrieves Contact objects with Id, Name, and Email fields
 * - CDC subscription: Listens for Contact change events in Salesforce
 * - Timer: Executes the contact fetch every 60 seconds
 *
 * How it works:
 * - The REST endpoint "/contacts" returns all contacts
 * - The REST endpoint "/contacts/{id}" returns a specific contact
 * - The CDC listener processes Contact change events from Salesforce
 * - Routes use direct endpoints for synchronous execution
 * - All Salesforce responses are unmarshaled into JSON format
 *
 * Note for beginners:
 * - @Component: Marks this class as a Spring component for automatic detection
 * - RestBindingMode.json: Automatically handles JSON conversion for REST endpoints
 * - synchronous=true: Ensures synchronous execution of the route
 * - direct: Creates in-memory synchronous calls between routes
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
                .to("direct:getContactById?synchronous=true"); // Route requests to direct:getContactById endpoint
        
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

        // Define route that listens for Salesforce CDC events for Contact objects
        from("salesforce:subscribe:data/ContactChangeEvent")
            .id("Listener Salesforce CDC events") // Set route ID for monitoring
            // Uncommented debug logging line
            // .to("log:debug?showAll=true&multiline=true");
            // Convert Salesforce response to JSON using Jackson library
            .unmarshal().json(JsonLibrary.Jackson)
            // Log the CDC event at INFO level
            .log(LoggingLevel.INFO, "A new event: ${body}"); 
        
        // Define timer-based route that runs every 10 seconds
        from("timer:fire?period=60000")                                     // Create timer trigger
            .id("Scheduler-based route: all contacts")                       // Set route ID for monitoring
            .to("direct:getContacts?synchronous=true")                      // Call the same contacts query route
            .log(LoggingLevel.INFO, "Salesforce response: ${body}");    // Log the response
    }
}
