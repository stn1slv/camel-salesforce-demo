package com.github.stn1slv.camel.sf.demo;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class SalesforceRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
        .component("servlet")
        .bindingMode(RestBindingMode.json);

        rest()
            .get("/contacts").id("Rest-based route")
            .to("direct:getContacts?synchronous=true");
        
        from("direct:getContacts")
            .to("salesforce:queryAll?sObjectQuery=SELECT Id, Name, Email FROM Contact")
            // .to("log:debug?showAll=true&multiline=true")
            .unmarshal().json(JsonLibrary.Jackson);
        
        from("timer:fire?period=10000")
            .id("Scheduler-based route")
            .to("direct:getContacts?synchronous=true")
            .log(LoggingLevel.INFO, "Salesforce response: ${body}");
    }
    
}
