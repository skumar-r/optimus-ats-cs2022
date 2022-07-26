package com.optimus.ats.service.remote;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.optimus.ats.dto.ApprovalDto;
import com.optimus.ats.dto.ProcessDto;


@RegisterRestClient
@Path("/")
public interface WorkflowRemoteRestService {
    
    @POST
    @Path("/approvals")
    @Produces(MediaType.APPLICATION_JSON)
    String post(ProcessDto request);

    @GET
    @Path("/approvals/{instanceId}/tasks")
    @Produces(MediaType.APPLICATION_JSON)
    String getTasks(@PathParam("instanceId") String instanceId);

    @POST
    @Path("/approvals/{instanceId}/ManagerApproval/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    String updateTaskStatus(@PathParam("instanceId") String instanceId, @PathParam("taskId") String taskId, ApprovalDto dto);
}
