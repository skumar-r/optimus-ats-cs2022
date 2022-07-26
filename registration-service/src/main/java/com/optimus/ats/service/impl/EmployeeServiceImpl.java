package com.optimus.ats.service.impl;

import com.optimus.ats.common.CommonResource;
import com.optimus.ats.common.FormData;
import com.optimus.ats.controller.EmployeeResource;
import com.optimus.ats.dto.EmployeeDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.service.EmployeeService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@ApplicationScoped
public class EmployeeServiceImpl extends CommonResource implements EmployeeService {

	static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	@Inject
	S3Client s3;

	@ConfigProperty(name = "ats.employee.photo-front-prefix")
	String photoFrontPrefix;

	@ConfigProperty(name = "ats.employee.photo-id-prefix")
	String photoIDPrefix;

	@ConfigProperty(name = "upload.directory")
	String uploadDir;

	@Transactional
	@Override
	public Response saveEmployee(EmployeeDto employee) throws IOException {
		Employee emp = getParseEmployeeDto(employee);
		Employee.persist(emp);
		if (employee.isHasS3Photo()) {
			FormData photoFrontObj = FormData.builder()
					.data(employee.getPhotoFrontFile())
					.filename(photoFrontPrefix + "_" + emp.getId())
					.mimetype("image/png")
					.build();
			FormData photoLeftObj = FormData.builder()
					.data(employee.getPhotoFrontFile())
					.filename(photoIDPrefix + "_" + emp.getId())
					.mimetype("image/png")
					.build();

			PutObjectResponse photoFront = s3.putObject(buildPutRequest(photoFrontObj),
					RequestBody.fromFile(employee.getPhotoFrontFile()));

			PutObjectResponse photoLeft = s3.putObject(buildPutRequest(photoLeftObj),
					RequestBody.fromFile(employee.getPhotoIDCardFile()));

			if (photoFront != null && photoLeft != null) {
				emp.setPhotoFront(photoFrontObj.getFilename());
				emp.setPhotoIdcard(photoLeftObj.getFilename());
				Employee.persist(emp);
				return Response.ok().status(Response.Status.CREATED).build();
			} else {
				return Response.serverError().build();
			}
		} else {
			File customDir = new File(uploadDir);
			log.info("local storage:" + uploadDir + "  " + customDir.getAbsolutePath());
			if (!customDir.exists()) {
				Path path = Paths.get(uploadDir);
				try{
					Files.createDirectories(path);
				} catch(Exception e){
					log.error("error", e);
				}
			}

			if (customDir.exists()) {
				log.info("local storage exists");
				String photoFrontFileName = customDir.getAbsolutePath() +
						File.separator + photoFrontPrefix + "_" + emp.getId() + ".png";
<<<<<<< .mine
				System.out.println(">>>>=" + employee.getEmail()+ " >>" +employee.getEmployeeName());
				System.out.println("photoFrontPrefix=" + photoFrontFileName+ " >>" +employee.getPhotoFrontFile().toPath());
=======
				log.info("photoFrontPrefix=" + photoFrontFileName);

>>>>>>> .theirs
				Files.write(Paths.get(photoFrontFileName), Files.readAllBytes(employee.getPhotoFrontFile().toPath()),
						StandardOpenOption.CREATE_NEW);
				String photoLeftFileName = customDir.getAbsolutePath() +
						File.separator + photoIDPrefix + "_" + emp.getId() + ".png";
				System.out.println("photoLeftPrefix=" + photoLeftFileName);
				Files.write(Paths.get(photoLeftFileName), Files.readAllBytes(employee.getPhotoIDCardFile().toPath()),
						StandardOpenOption.CREATE_NEW);

				if (StringUtils.isNotBlank(photoFrontFileName) && StringUtils.isNotBlank(photoLeftFileName)) {
					System.out.println("isNotBlank");
					emp.setPhotoFront(photoFrontFileName);
					emp.setPhotoIdcard(photoLeftFileName);
					Employee.persist(emp);
					return Response.ok().status(Response.Status.CREATED).build();
				} else {
					return Response.serverError().build();
				}
			}
			return Response.serverError().build();
		}
	}

	public Employee getParseEmployeeDto(EmployeeDto employee) {
		Employee emp = new Employee();
		emp.setEmployeeName(employee.getEmployeeName());
		emp.setEmail(employee.getEmail());
		emp.setMobile(employee.getMobile());
		emp.setHasS3Photo(employee.isHasS3Photo());
		emp.setDepartment(employee.getDepartment());
		emp.setDesignation(employee.getDesignation());
		emp.setCsEmployeeId(employee.getCsEmployeeId());
		return emp;
	}
}
