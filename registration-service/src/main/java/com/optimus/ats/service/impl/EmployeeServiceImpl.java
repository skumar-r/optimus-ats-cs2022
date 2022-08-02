package com.optimus.ats.service.impl;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.optimus.ats.common.CommonResource;
import com.optimus.ats.common.EventService;
import com.optimus.ats.common.FormData;
import com.optimus.ats.common.ServiceResponse;
import com.optimus.ats.dto.EmployeeDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.service.EmployeeService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class EmployeeServiceImpl extends CommonResource implements EmployeeService {

	static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	@Inject
	S3Client s3;
	@Inject
	EventService event;

	/*@ConfigProperty(name = "aws.accessKeyId")
	String ACCESS_KEY;

	@ConfigProperty(name = "aws.secretAccessKey")
	String SECRET_KEY;*/

	@ConfigProperty(name = "quarkus.s3.aws.region")
	String region;

	@ConfigProperty(name = "ats.employee.photo-front-prefix")
	String photoFrontPrefix;

	@ConfigProperty(name = "ats.employee.photo-id-prefix")
	String photoIDPrefix;

	@ConfigProperty(name = "upload.directory.employee")
	String uploadDir;

	@Transactional
	@Override
	public ServiceResponse saveEmployee(EmployeeDto employee) throws IOException {
		ServiceResponse response = ServiceResponse.createSuccessServiceResponse();
		if (!Objects.isNull(Employee.findByName(employee.getCsEmployeeId())) && StringUtils.equals(Employee.findByName(employee.getCsEmployeeId()).getCsEmployeeId(), employee.getCsEmployeeId())) {
			response.getContentMap().put("message", "Employee Id already exists");
			response.setSuccess(false);
			return response;
		}
		if (Objects.isNull(employee.getPhotoFrontFile()) && Objects.isNull(employee.getPhotoIDCardFile())) {
			response.getContentMap().put("message", "Both employee Image with Face and ID Card Front Photo are mandatory.Please upload them");
			response.setSuccess(false);
			return response;
		}

		if (!Objects.isNull(employee.getPhotoFrontFile()) && !Objects.isNull(employee.getPhotoIDCardFile()) && (!hasDetectFacesinImage(employee.getPhotoFrontFile()) || !hasDetectFacesinImage(employee.getPhotoIDCardFile()))) {
			response.getContentMap().put("message", "Uploaded Photo images(s) don't contain face.");
			response.setSuccess(false);
			return response;
		}

		Employee emp = getParseEmployeeDto(employee);
		Employee.persist(emp);
		if (employee.isHasS3Photo()) {
			FormData photoFrontObj = FormData.builder()
					.data(employee.getPhotoFrontFile())
					.filename(photoFrontPrefix + "_" + emp.getId())
					.mimetype("image/png")
					.build();
			FormData photoLeftObj = FormData.builder()
					.data(employee.getPhotoIDCardFile())
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
				return response;
			} else {
				Employee.deleteById(emp.getId());
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
						File.separator + photoFrontPrefix + "_" + emp.getId() + ".png";
				log.info(">>>>=" + employee.getEmail() + " >>" + employee.getEmployeeName());
				log.info("photoFrontPrefix=" + photoFrontFileName + " >>" + employee.getPhotoFrontFile().toPath());

				log.info("photoFrontPrefix=" + photoFrontFileName);

				Files.write(Paths.get(photoFrontFileName), Files.readAllBytes(employee.getPhotoFrontFile().toPath()),
						StandardOpenOption.CREATE_NEW);
				String photoLeftFileName = customDir.getAbsolutePath() +
						File.separator + photoIDPrefix + "_" + emp.getId() + ".png";
				log.info("photoLeftPrefix=" + photoLeftFileName);
				Files.write(Paths.get(photoLeftFileName), Files.readAllBytes(employee.getPhotoIDCardFile().toPath()),
						StandardOpenOption.CREATE_NEW);

				if (StringUtils.isNotBlank(photoFrontFileName) && StringUtils.isNotBlank(photoLeftFileName)) {
					log.info("Photo fileNames are not blank");
					emp.setPhotoFront(FilenameUtils.separatorsToSystem(photoFrontFileName));
					emp.setPhotoIdcard(FilenameUtils.separatorsToSystem(photoLeftFileName));
					Employee.persist(emp);
					return response;
				} else {
					log.info("Photo fileNames are  blank");
					Employee.deleteById(emp.getId());
					response.setSuccess(false);
					response.getContentMap().put("message", "Please fill required fields");
					return response;
				}
			}
			Employee.deleteById(emp.getId());
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
		return "data:image/png;base64,"+ Base64.getEncoder().encodeToString(fileContent);
	}
	public String getS3Photo(String photoUrl) {
		byte[] fileContent = new byte[0];
		log.info("Try to get the photo from S3"+photoUrl);
		try {
			fileContent = s3.getObject(buildGetRequest(photoUrl)).readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "data:image/png;base64,"+ Base64.getEncoder().encodeToString(fileContent);
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

	private boolean hasDetectFacesinImage(File sourceImage) throws IOException {
		try (InputStream targetImgStream = new FileInputStream(sourceImage)) {
			/*BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
			AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();*/
			AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(region).build();
			DetectFacesRequest request = new DetectFacesRequest()
					.withImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream))))
					.withAttributes(Attribute.ALL);
			DetectFacesResult result = rekognitionClient.detectFaces(request);
			List<FaceDetail> faceDetails = result.getFaceDetails();
			event.eventLog("Detect Face in Image", null, convertListToJSONArray(faceDetails));
			return faceDetails.size() > 0;
		} catch (AmazonRekognitionException | FileNotFoundException e) {
			log.error("Exception", e);
			return false;
		}
	}

	private String convertListToJSONArray(List<FaceDetail> list) {
		JsonArray array = new JsonArray();
		list.forEach(detail -> array.add(JsonObject.mapFrom(detail)));
		return array.toString();
	}
}
