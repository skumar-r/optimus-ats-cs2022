package com.optimus.ats;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.eventbus.EventBus;


import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/validation/api")
public class ValidationResource {

    /*private final Vertx vertx;

    @Inject
    public ValidationResource(Vertx vertx) {
        this.vertx = vertx;
    }*/

    @Inject
    EventBus bus;
    
    @GET
    @Path("/echocopy")
    public Uni<String> hello(@QueryParam("name") String name) {     
        return bus.<String>request("echo", name)               
                .onItem().transform(response -> response.body().toUpperCase());   
    }
}