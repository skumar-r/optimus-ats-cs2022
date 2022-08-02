package com.optimus.ats.controller;

import com.optimus.ats.common.EventService;
import com.optimus.ats.common.ServiceResponse;
import com.optimus.ats.dto.VehicleDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.model.Vehicle;
import com.optimus.ats.service.impl.VehicleServiceImpl;
import org.jboss.resteasy.reactive.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

@ApplicationScoped
@Path("/vehicle")
public class VehicleResource {
	static final Logger log = LoggerFactory.getLogger(VehicleResource.class);
	@Inject
	EventService event;

	@Inject
	VehicleServiceImpl vehicleService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		List<Vehicle> vehicles = Vehicle.listAll();
		vehicles = vehicles.stream().map(vehicle -> {
			Employee emp = Employee.findById(vehicle.getEmployeeId());
			if (vehicle.isHasS3Photo()) {
				vehicle.setVehiclePhoto(vehicleService.getS3Photo(vehicle.getPhotoFront()));
			} else {
				vehicle.setVehiclePhoto(vehicleService.getLocalPhoto(vehicle.getPhotoFront()));
			}
			vehicle.setEmplyeeName(emp.getEmployeeName());
			vehicle.setCsEmployeeId(emp.getCsEmployeeId());
			return vehicle;
		}).collect(Collectors.toList());
		return Response.ok(vehicles).build();
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
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MULTIPART_FORM_DATA)
	public ServiceResponse create(@MultipartForm VehicleDto vehicleDto) {
		System.out.println("Reg No:" + vehicleDto.getRegNo());
		ServiceResponse response = null;
		event.eventLog("Save Employee - Request", vehicleDto, "");
		try {
			return vehicleService.saveVehicle(vehicleDto);
		} catch (Exception e) {
			log.error("exception", e);
			response = ServiceResponse.createFailureServiceResponse();
		}
		event.eventLog("Save Employee - Response", response, "");
		return response;
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
