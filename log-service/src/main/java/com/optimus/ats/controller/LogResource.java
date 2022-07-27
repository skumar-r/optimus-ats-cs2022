package com.optimus.ats.controller;

import com.optimus.ats.model.LogManager;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import io.vertx.mutiny.core.eventbus.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
@Path("/log")
public class LogResource {

	@Inject
	EventBus bus;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		List<LogManager> employees = LogManager.listAll();
		return Response.ok(employees).build();
	}

	@GET
	@Path("/{serviceName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getForService(@PathParam("serviceName") String serviceName) {
		if(Objects.requireNonNullElse(serviceName, "all").equals("all") || serviceName.trim().length()<=0 ){
			return getAll();
		}
		List<LogManager> employees = LogManager.list("serviceName=?1", serviceName);
		return Response.ok(employees).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void create(LogManager dto) {
		bus.<LogManager>publish("log", JsonObject.mapFrom(dto));
	}
}
