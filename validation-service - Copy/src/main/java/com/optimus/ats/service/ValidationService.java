package com.optimus.ats.service;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.mutiny.core.Vertx;
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
    Vertx vertx;

    public void init(@Observes StartupEvent ev) {
        log.info("init with startup event");
        vertx.eventBus().consumer("echo1")
                .handler(h -> {
                    log.info("received: {}", h.body());
                    h.reply("Hello back from consumer: " + h.body());
                });
    }
    @ConsumeEvent("echo1")              
    public String echo(String name) {      
        log.info("echo1-->"+name);
        return name;             
    }
}
