package com.optimus.ats.service;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.eventbus.EventBus;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped                          
public class ValidationService {

    static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    @Inject
    EventBus bus;

    @ConsumeEvent("echo")              
    public String echo(String name) {      
        return name;             
    }
}
