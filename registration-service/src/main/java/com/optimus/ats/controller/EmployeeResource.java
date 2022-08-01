package com.optimus.ats.controller;

import com.optimus.ats.common.EventService;
import com.optimus.ats.common.ServiceResponse;
import com.optimus.ats.dto.EmployeeDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.service.EmployeeService;
import com.optimus.ats.service.impl.EmployeeServiceImpl;
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
@Path("/employee")
public class EmployeeResource {

	static final Logger log = LoggerFactory.getLogger(EmployeeResource.class);

	@Inject
	EventService event;

	@Inject
	EmployeeService employeeService;

	@Inject
	EmployeeServiceImpl employeeServiceImpl;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		List<Employee> employees = Employee.listAll();
		employees = employees.stream().map(employee -> {
			if (employee.isHasS3Photo()) {
				employee.setEmpPhoto(employeeServiceImpl.getS3Photo(employee.getPhotoFront()));
			} else {
				employee.setEmpPhoto(employeeServiceImpl.getLocalPhoto(employee.getPhotoFront()));
			}
			return employee;
		}).collect(Collectors.toList());
		return Response.ok(employees).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") Long id) {
		return Employee.findByIdOptional(id)
				.map(emp -> Response.ok(emp).build())
				.orElse(Response.status(Response.Status.NOT_FOUND).build());
	}

	@POST
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MULTIPART_FORM_DATA)
	public ServiceResponse create(@MultipartForm EmployeeDto employee) {
		System.out.println("email:" + employee.getEmail());
		System.out.println("name:" + employee.getEmployeeName());
		ServiceResponse response = null;
		event.eventLog("Save Employee - Request", employee, "");
		try {
			response = employeeService.saveEmployee(employee);
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
		boolean deleted = Employee.deleteById(id);
		if (deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
