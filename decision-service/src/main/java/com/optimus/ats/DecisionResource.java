package com.optimus.ats;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

@Path("/decision")
public class DecisionResource {

    @GET
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject create() {
        return new JSONObject();
    }
}