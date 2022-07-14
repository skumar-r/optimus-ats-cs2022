package com.optimus.ats.controller;

import com.optimus.ats.dto.VehicleDto;
import com.optimus.ats.model.Vehicle;
import com.optimus.ats.service.VehicleService;
import org.jboss.resteasy.reactive.MultipartForm;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

@ApplicationScoped
@Path("/vehicle-registration")
public class VehicleResource {

	@Inject
	VehicleService vehicleService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		List<Vehicle> employees = Vehicle.listAll();
		return Response.ok(employees).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") Long id) {
		return Vehicle.findByIdOptional(id)
				.map(emp -> Response.ok(emp).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MULTIPART_FORM_DATA)
	public Response create(@MultipartForm VehicleDto vehicleDto) {
		System.out.println("Reg No:" + vehicleDto.getRegNo());
		try {
			return vehicleService.saveVehicle(vehicleDto);
		} catch (IOException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response deleteById(@PathParam("id") Long id) {
		boolean deleted = Vehicle.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
