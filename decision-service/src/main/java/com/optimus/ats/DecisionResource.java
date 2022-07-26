package com.optimus.ats;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.optimus.ats.service.DecisionService;

@Path("/approval")
public class DecisionResource {

    @Inject
    DecisionService decisionService;
    
    @POST
    @Path("/create/dummy/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject create1(@PathParam("id") Long employeeId) {
        decisionService.createDummyRecord(employeeId);
        return new JSONObject();
    }

    @GET
    @Path("/create1")
    @Produces(MediaType.APPLICATION_JSON)
    public JSONObject create2() {
        return new JSONObject();
    }
}