package com.optimus.ats.controller;

import com.optimus.ats.dto.EmployeeDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.service.EmployeeService;
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
@Path("/employee")
public class EmployeeResource {

	@Inject
	EmployeeService employeeService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		List<Employee> employees = Employee.listAll();
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
	public Response create(@MultipartForm EmployeeDto employee) {
		System.out.println("email:"+employee.getEmail());
		try {
			return employeeService.saveEmployee(employee);
		} catch (IOException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response deleteById(@PathParam("id") Long id) {
		boolean deleted = Employee.deleteById(id);
		if(deleted) {
			return Response.noContent().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
