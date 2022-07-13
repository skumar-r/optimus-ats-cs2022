package com.optimus.ats.service.impl;

import com.optimus.ats.common.CommonResource;
import com.optimus.ats.common.FormData;
import com.optimus.ats.dto.EmployeeDto;
import com.optimus.ats.dto.VehicleDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.model.Vehicle;
import com.optimus.ats.service.EmployeeService;
import com.optimus.ats.service.VehicleService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@ApplicationScoped
public class VehicleServiceImpl extends CommonResource implements VehicleService {
	@Inject
	S3Client s3;

	@ConfigProperty(name = "ats.vehicle.photo-front-prefix")
	String photoFrontPrefix;

	@ConfigProperty(name = "ats.vehicle.photo-rear-prefix")
	String photoRearPrefix;

	@ConfigProperty(name = "upload.directory")
	String uploadDir;

	@Transactional
	@Override
	public Response saveVehicle(VehicleDto vehicleDto) throws IOException {
		Vehicle veh = getParseVehicleDto(vehicleDto);
		Vehicle.persist(veh);
		if (vehicleDto.isHasS3Photo()) {
			FormData photoFrontObj = FormData.builder()
					.data(vehicleDto.getPhotoFrontFile())
					.filename(photoFrontPrefix + "_" + veh.getId())
					.mimetype("image/png")
					.build();
			FormData photoRearObj = FormData.builder()
					.data(vehicleDto.getPhotoRearFile())
					.filename(photoRearPrefix + "_" + veh.getId())
					.mimetype("image/png")
					.build();

			PutObjectResponse photoFront = s3.putObject(buildPutRequest(photoFrontObj),
					RequestBody.fromFile(vehicleDto.getPhotoFrontFile()));

			PutObjectResponse photoRear = s3.putObject(buildPutRequest(photoRearObj),
					RequestBody.fromFile(vehicleDto.getPhotoRearFile()));


			if (photoFront != null && photoRear != null) {
				veh.setPhotoFront(photoFrontObj.getFilename());
				veh.setPhotoRear(photoRearObj.getFilename());
				Vehicle.persist(veh);
				return Response.ok().status(Response.Status.CREATED).build();
			} else {
				return Response.serverError().build();
			}
		} else{
			File customDir = new File(uploadDir);
			System.out.println("local storage:"+ uploadDir+"  "+customDir.getAbsolutePath());
			if(customDir.exists()) {
				System.out.println("local storage exists");
				String photoFrontFileName = customDir.getAbsolutePath() +
						File.separator + photoFrontPrefix + "_" + veh.getId() + ".png";
				System.out.println("photoFrontPrefix="+ photoFrontFileName);
				Files.write(Paths.get(photoFrontFileName), Files.readAllBytes(vehicleDto.getPhotoFrontFile().toPath()),
						StandardOpenOption.CREATE_NEW);
				String photoRearFileName = customDir.getAbsolutePath() +
						File.separator + photoRearPrefix + "_" + veh.getId() + ".png";
				System.out.println("photoRearPrefix="+ photoRearFileName);
				Files.write(Paths.get(photoRearFileName), Files.readAllBytes(vehicleDto.getPhotoRearFile().toPath()),
						StandardOpenOption.CREATE_NEW);

				if (StringUtils.isNotBlank(photoFrontFileName) && StringUtils.isNotBlank(photoRearFileName)) {
					System.out.println("isNotBlank=");
					veh.setPhotoFront(photoFrontFileName);
					veh.setPhotoRear(photoRearFileName);
					Vehicle.persist(veh);
					return Response.ok().status(Response.Status.CREATED).build();
				} else {
					return Response.serverError().build();
				}
			}
			return Response.serverError().build();
		}
	}
	public Vehicle getParseVehicleDto(VehicleDto vehicleDto) {
		Vehicle veh =  new Vehicle();
		veh.setRegNo(vehicleDto.getRegNo());
		veh.setVehicleDetails(vehicleDto.getVehicleDetails());
		veh.setEmployeeId(vehicleDto.getEmployeeId());
		veh.setHasS3Photo(vehicleDto.isHasS3Photo());
		return veh;
	}
}
