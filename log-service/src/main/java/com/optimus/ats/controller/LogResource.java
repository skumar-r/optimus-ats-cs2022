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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

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

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void create(LogManager dto) {
		bus.<LogManager>publish("log", JsonObject.mapFrom(dto));
	}
}
