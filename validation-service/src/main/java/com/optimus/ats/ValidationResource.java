package com.optimus.ats;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import com.optimus.ats.dto.ApprovalDto;
import com.optimus.ats.dto.ResponseDto;
import com.optimus.ats.model.DecisionWorkflowRequest;
import com.optimus.ats.service.ValidationService;

@Path("/validation/api")
public class ValidationResource {

    /*private final Vertx vertx;

    @Inject
    public ValidationResource(Vertx vertx) {
        this.vertx = vertx;
    }*/

    @Inject
    EventBus bus;

    @Inject
    ValidationService validationService;
    
    @GET
    @Path("/echo")
    public Uni<String> hello(@QueryParam("name") String name) {     
        return bus.<String>request("echo1", name)               
                .onItem().transform(response -> response.body().toUpperCase());   
    }

    @POST
    @Path("/decision/dummy/{employeeId}/{managerId}")
    public String hello2(@PathParam("employeeId") Long employeeId, @PathParam("managerId") Long managerId) throws Exception {     
        return validationService.invokeDecisionService(employeeId, managerId);
    }

    @GET
    @Path("/workflow/all")
    public List<DecisionWorkflowRequest> getAllWorkflows() throws Exception {     
        return validationService.getAll();
    }

    @POST
    @Path("/workflow/approve")
    public ResponseDto approve(ApprovalDto dto) throws Exception {     
        return validationService.approve(dto);
    }
}