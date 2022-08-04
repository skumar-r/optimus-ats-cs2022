package com.optimus.ats.service.impl;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.DetectTextRequest;
import com.amazonaws.services.rekognition.model.DetectTextResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.optimus.ats.common.CommonResource;
import com.optimus.ats.common.EventService;
import com.optimus.ats.common.FormData;
import com.optimus.ats.common.ServiceResponse;
import com.optimus.ats.dto.VehicleDto;
import com.optimus.ats.model.Vehicle;
import com.optimus.ats.service.VehicleService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class VehicleServiceImpl extends CommonResource implements VehicleService {

	static final Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);

	@Inject
	S3Client s3;
	@Inject
	EventService event;

	@ConfigProperty(name = "ats.vehicle.photo-front-prefix")
	String photoFrontPrefix;

	@ConfigProperty(name = "upload.directory.vehicle")
	String uploadDir;

	@ConfigProperty(name = "quarkus.s3.aws.region")
	String region;

	@Transactional
	@Override
	public ServiceResponse saveVehicle(VehicleDto vehicleDto) throws IOException {
		ServiceResponse response = ServiceResponse.createSuccessServiceResponse();
		if (!Objects.isNull(Vehicle.findByName(vehicleDto.getRegNo())) && StringUtils.equals(Vehicle.findByName(vehicleDto.getRegNo()).getRegNo(), vehicleDto.getRegNo())) {
			response.getContentMap().put("message", "Vehicle Reg no already exists");
			response.setSuccess(false);
			return response;
		}
		if (!Objects.isNull(Vehicle.findByEmployeeId(vehicleDto.getEmployeeId()))) {
			response.getContentMap().put("message", "Employee already exists");
			response.setSuccess(false);
			return response;
		}
		if (Objects.isNull(vehicleDto.getPhotoFrontFile())) {
			response.getContentMap().put("message", "Vehicle Front Photo are mandatory.Please upload it");
			response.setSuccess(false);
			return response;
		}
		if (!Objects.isNull(vehicleDto.getPhotoFrontFile()) && StringUtils.isEmpty(getDetectedText(vehicleDto.getPhotoFrontFile()))) {
			response.getContentMap().put("message", "Uploaded Photo images(s) don't contain Registration No.");
			response.setSuccess(false);
			return response;
		}
		Vehicle veh = getParseVehicleDto(vehicleDto);
		Vehicle.persist(veh);
		if (vehicleDto.isHasS3Photo()) {
			FormData photoFrontObj = FormData.builder()
					.data(vehicleDto.getPhotoFrontFile())
					.filename(photoFrontPrefix + "_" + veh.getId())
					.mimetype("image/png")
					.build();

			PutObjectResponse photoFront = s3.putObject(buildPutRequest(photoFrontObj),
					RequestBody.fromFile(vehicleDto.getPhotoFrontFile()));

			if (photoFront != null) {
				veh.setPhotoFront(photoFrontObj.getFilename());
				Vehicle.persist(veh);
				return response;
			} else {
				Vehicle.deleteById(veh.getId());
				response.setSuccess(false);
				response.getContentMap().put("message", "Please fill required fields");
				return response;
			}
		} else {
			File customDir = new File(uploadDir);
			log.info("local storage:" + uploadDir + "  " + customDir.getAbsolutePath());
			if (!customDir.exists()) {
				Path path = Paths.get(uploadDir);
				try {
					Files.createDirectories(path);
				} catch (Exception e) {
					log.error("error", e);
				}
			}
			if (customDir.exists()) {
				log.info("local storage exists");
				String photoFrontFileName = customDir.getAbsolutePath() +
						File.separator + photoFrontPrefix + "_" + veh.getId() + ".png";
				log.info("photoFrontPrefix=" + photoFrontFileName);
				Files.write(Paths.get(photoFrontFileName), Files.readAllBytes(vehicleDto.getPhotoFrontFile().toPath()),
						StandardOpenOption.CREATE_NEW);

				if (StringUtils.isNotBlank(photoFrontFileName)) {
					log.info("Photo fileNames are not blank");
					veh.setPhotoFront(photoFrontFileName);
					Vehicle.persist(veh);
					return response;
				} else {
					log.info("Photo fileNames are  blank");
					Vehicle.deleteById(veh.getId());
					response.setSuccess(false);
					response.getContentMap().put("message", "Please fill required fields");
					return response;
				}
			}
			Vehicle.deleteById(veh.getId());
			return response;
		}
	}

	public String getLocalPhoto(String photoUrl) {
		byte[] fileContent = new byte[0];
		try {
			fileContent = FileUtils.readFileToByteArray(new File(photoUrl));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent);
	}

	public String getS3Photo(String photoUrl) {
		byte[] fileContent = new byte[0];
		try {
			fileContent = s3.getObject(buildGetRequest(photoUrl)).readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent);
	}

	public Vehicle getParseVehicleDto(VehicleDto vehicleDto) {
		Vehicle veh = new Vehicle();
		veh.setRegNo(vehicleDto.getRegNo());
		veh.setVehicleDetails(vehicleDto.getVehicleDetails());
		veh.setEmployeeId(vehicleDto.getEmployeeId());
		veh.setHasS3Photo(vehicleDto.isHasS3Photo());
		return veh;
	}

	private String getDetectedText(File targetImage) {
		TextDetection detectedText = null;
		try (InputStream targetImgStream = new FileInputStream(targetImage)) {
			AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(region).build();
			DetectTextRequest request = new DetectTextRequest()
					.withImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream))));
			DetectTextResult result = rekognitionClient.detectText(request);
			List<TextDetection> textDetections = result.getTextDetections();
			detectedText = textDetections.stream().filter(txt -> (txt.getDetectedText().contains("TN") || txt.getDetectedText().contains("KL")))
					.findFirst().get();
			event.eventLog("AWS-Text Extraction Response", null, !Objects.isNull(detectedText) ? detectedText.getDetectedText() : "");
		} catch (Exception e) {
			log.error("Exception", e);
			e.printStackTrace();
		}
		return !Objects.isNull(detectedText) ? detectedText.getDetectedText() : "";
	}
}
