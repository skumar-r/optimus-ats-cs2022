package com.optimus.ats.controller;

import com.optimus.ats.common
		.ServiceResponse;
import com.optimus.ats.common.StatusType;
import com.optimus.ats.dto.RecognitionDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.model.Vehicle;
import com.optimus.ats.service.RecognitionService;
import com.optimus.ats.service.ValidationService;

import com.optimus.ats.service.impl.RecognitionServiceImpl;
import org.jboss.resteasy.reactive.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Path("/recognition")
public class RecognitionResource {

	static final Logger log = LoggerFactory.getLogger(RecognitionResource.class);

	@Inject
	RecognitionServiceImpl recognitionService;
	@Inject
	ValidationService validationService;

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
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MULTIPART_FORM_DATA)
	public ServiceResponse validateEmployee(@MultipartForm RecognitionDto dto) throws IOException {
		log.info("RecognitionDto->Type:" + dto.getType());
		ServiceResponse response= recognitionService.validateEmployee(dto);
		log.info("Status Type:" + response.getContentMap().get("StatusType"));
		if(response.getContentMap().get("StatusType").equals(StatusType.APPROVAL_REQUIRED.getType())){			
			Employee emp = (Employee)response.getContentMap().get("employee");
			log.info("Invoking Decision Workflow Service for employee Id:{}", emp.getId());
			recognitionService.storeApproveRequiredEmpPhoto(dto.getFormData(),emp.getCsEmployeeId(),emp.isHasS3Photo());
			validationService.invokeDecisionService(emp.getId(), null, emp.getCsEmployeeId());
		}
		return response;		
	}

	@POST
	@Path("/vehicle")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MULTIPART_FORM_DATA)
	public ServiceResponse validateVehicle(@MultipartForm RecognitionDto dto) throws IOException {
		log.info("RecognitionDto->Type:" + dto.getType());
		ServiceResponse response= recognitionService.validateVehicle(dto);
		log.info("Status Type:" + response.getContentMap().get("StatusType"));
		/*if(response.getContentMap().get("StatusType").equals(StatusType.APPROVAL_REQUIRED.getType())){
			Vehicle vehicle = (Vehicle)response.getContentMap().get("vehicle");
			Employee emp = (Employee)response.getContentMap().get("employee");
			log.info("Invoking Decision Workflow Service for employee Id:{}", vehicle.getId());
			recognitionService.storeApproveRequiredEmpPhoto(dto.getFormData(),vehicle.getRegNo(),emp.isHasS3Photo());
			validationService.invokeDecisionService(emp.getId(), null, emp.getCsEmployeeId());
		}*/
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
