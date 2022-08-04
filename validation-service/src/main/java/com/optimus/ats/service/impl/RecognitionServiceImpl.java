package com.optimus.ats.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.optimus.ats.common.*;
import com.optimus.ats.dto.RecognitionDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.model.Vehicle;
import com.optimus.ats.service.RecognitionService;
import com.optimus.ats.service.ValidationService;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class RecognitionServiceImpl extends CommonResource implements RecognitionService {

	static final Logger log = LoggerFactory.getLogger(RecognitionServiceImpl.class);
	@Inject
	S3Client s3;
	@Inject
	EventService event;
	@Inject
	ValidationService validationService;
	
	@ConfigProperty(name = "bucket.name")
	String bucketName;

	/*@ConfigProperty(name = "aws.accessKeyId")
	String ACCESS_KEY;
	@ConfigProperty(name = "aws.secretAccessKey")
	String SECRET_KEY;*/

	@ConfigProperty(name = "quarkus.s3.aws.region")
	String region;

	@ConfigProperty(name = "upload.directory.employee")
	String uploadDir;

	@Transactional
	@Override
	public ServiceResponse validateEmployee(RecognitionDto dto) throws IOException {
		ServiceResponse response = ServiceResponse.createSuccessServiceResponse();
		event.eventLog("Validate Employee - Request", dto, "");	
		try {
			if (dto != null && dto.getFormData().exists() &&  dto.getIdCardData().exists()) {
				if (StringUtils.equals("employee", dto.getType()) && hasDetectFacesinImage(dto.getFormData()) && hasDetectFacesinImage(dto.getIdCardData())) {
					return getEmployeeMatchedRecord(dto.getIdCardData(), dto.getFormData());
				} else{
					response.setSuccess(false);
					response.getContentMap().put("StatusType",StatusType.NO_FACE.getType());
					response.getContentMap().put("message","Uploaded image does not contain face.");
				}
			} else {
				response.setSuccess(false);
				response.getContentMap().put("StatusType",StatusType.NOT_FOUND.getType());
				response.getContentMap().put("message","Employee Photo and ID card required");
			}
		} catch (Exception e) {
			log.error("validateEmployee:",e);
			response.setSuccess(false);
			response.getContentMap().put("StatusType",StatusType.ERROR.getType());
			response.getContentMap().put("message","Employee Photo and ID card required");
		}
		event.eventLog("Validate Employee - Response", response, "");	
		return response;
	}

	private ServiceResponse getEmployeeMatchedRecord(File idCardImage, File faceImage) throws IOException {
		ServiceResponse response = ServiceResponse.createSuccessServiceResponse();
		List<String> extractedText = getDetectedText(idCardImage);
		log.info("Extracted Text: {}",extractedText.size());
		String csId = extractedText.stream().filter(item -> item.contains("CS")).findFirst().orElse(null);
		log.info("Extracted csEmployeeId>>:{}", csId);
		Employee employee = null;
		if (StringUtils.isNotBlank(csId)) {
			List<Employee> employees = Employee.listAll();
			employee = employees.stream().filter(empl -> StringUtils.equals(empl.getCsEmployeeId(), csId)).findFirst().get();

			if(Objects.isNull(employee)){
				/// not found
				response.getContentMap().put("StatusType",StatusType.NO_MATCH.getType());
				response.setSuccess(false);
				response.getContentMap().put("message","No Employee found with the Employee ID :"+csId);
			} else{
				log.info("employee Name>>"+employee.getEmployeeName());
				log.info("employee Image>>"+employee.getPhotoFront());
				response.getContentMap().put("employee",employee);
				response.getContentMap().put("empPhoto",employee.isHasS3Photo()?getS3Photo(employee.getPhotoFront()):getLocalPhoto(employee.getPhotoFront()));
				if(hasFaceMatched(employee.isHasS3Photo(), employee.getPhotoFront(), faceImage)){
					// matched
					log.info("employee Match >> True");
					response.getContentMap().put("StatusType",StatusType.FULL_MATCH.getType());
				} else {
					// no match and call decision service
					log.info("employee Match >> False");

					response.setSuccess(true);
					response.getContentMap().put("message","Employee face not matched");
					response.getContentMap().put("StatusType",StatusType.APPROVAL_REQUIRED.getType());
				}
			}
		} else {
			response.setSuccess(false);
			response.getContentMap().put("message","Unable to get CS Employee Id from the uploaded ID Card Photo Image. Please upload a valid ID card image");
			response.getContentMap().put("StatusType",StatusType.ERROR.getType());
		}
		return response;
	}

	@Transactional
	@Override
	public ServiceResponse validateVehicle(RecognitionDto dto) throws IOException {
		ServiceResponse response = ServiceResponse.createSuccessServiceResponse();
		event.eventLog("Validate Vehicle - Request", dto, "");
		try {
			if (dto != null && dto.getFormData().exists() &&  dto.getIdCardData().exists()) {
				String detectedVehNo = getVehicleDetectedText(dto.getFormData());
				if (StringUtils.equals("vehicle", dto.getType()) && (detectedVehNo.contains("TN") || detectedVehNo.contains("KL"))) {
					return getVehicleMatchedRecord(dto.getIdCardData(), dto.getFormData());
				} else{
					response.setSuccess(false);
					response.getContentMap().put("StatusType",StatusType.NO_VEHICLE.getType());
					response.getContentMap().put("message","Uploaded image does not contain vehicle.");
				}
			} else {
				response.setSuccess(false);
				response.getContentMap().put("StatusType",StatusType.NOT_FOUND.getType());
				response.getContentMap().put("message","Vehicle Photo and ID card required");
			}
		} catch (Exception e) {
			log.error("validate Vehicle:",e);
			response.setSuccess(false);
			response.getContentMap().put("StatusType",StatusType.ERROR.getType());
			response.getContentMap().put("message","Vehicle Photo and ID card required");
		}
		event.eventLog("Validate Vehicle - Response", response, "");
		return response;
	}

	private ServiceResponse getVehicleMatchedRecord(File idCardImage, File vehicleImage) throws IOException {
		ServiceResponse response = ServiceResponse.createSuccessServiceResponse();
		List<String> extractedText = getDetectedText(idCardImage);
		log.info("Extracted Text: {}", extractedText.size());
		String csId = extractedText.stream().filter(item -> item.contains("CS")).findFirst().orElse(null);
		log.info("Extracted csEmployeeId>>:{}", csId);
		Employee employee = null;
		if (StringUtils.isNotBlank(csId)) {
			List<Employee> employees = Employee.listAll();
			employee = employees.stream().filter(empl -> StringUtils.equals(empl.getCsEmployeeId(), csId)).findFirst().orElse(null);
			Vehicle vehicle = Objects.isNull(employee)? null: Vehicle.findByEmployeeId(employee.getId());
			if (Objects.isNull(employee)) {
				/// not found
				response.getContentMap().put("StatusType", StatusType.NO_MATCH.getType());
				response.setSuccess(false);
				response.getContentMap().put("message", "No Employee found with the Employee ID :" + csId);
			} else {
				log.info("employee Name>>" + employee.getEmployeeName());
				log.info("employee Image>>" + employee.getPhotoFront());
				response.getContentMap().put("employee", employee);
				response.getContentMap().put("vehicle", vehicle);
				response.getContentMap().put("empPhoto", employee.isHasS3Photo() ? getS3Photo(vehicle.getPhotoFront()) : getLocalPhoto(vehicle.getPhotoFront()));
				if (getVehicleDetectedText(vehicleImage).contains(vehicle.getRegNo())) {
					// matched
					log.info("employee Match >> True");
					response.getContentMap().put("StatusType", StatusType.FULL_MATCH.getType());
				} else {
					// no match and call decision service
					log.info("employee Match >> False");

					response.setSuccess(true);
					response.getContentMap().put("message", "Employee face not matched");
					response.getContentMap().put("StatusType", StatusType.APPROVAL_REQUIRED.getType());
				}
			}
		} else {
			response.setSuccess(false);
			response.getContentMap().put("message", "Unable to get CS Employee Id from the uploaded ID Card Photo Image. Please upload a valid ID card image");
			response.getContentMap().put("StatusType", StatusType.ERROR.getType());
		}
		return response;
	}
	private String getVehicleDetectedText(File targetImage) {
		StringBuilder stringBuilder = new StringBuilder();
		TextDetection detectedText = null;
		try (InputStream targetImgStream = new FileInputStream(targetImage)) {
			AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(region).build();
			DetectTextRequest request = new DetectTextRequest()
					.withImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream))));
			DetectTextResult result = rekognitionClient.detectText(request);
			List<TextDetection> textDetections = result.getTextDetections();
			for (TextDetection text : textDetections) {
				log.info("Detected: " + text.getDetectedText());
				stringBuilder.append(text.getDetectedText());
			}
			log.info("detexted text==", stringBuilder.toString());
			event.eventLog("AWS-Text Extraction Response", null, stringBuilder.toString());
		} catch (Exception e) {
			log.error("Exception", e);
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
	private boolean hasFaceMatched(boolean hasS3Photo, String sourceImage, File targetImage) {
		//BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
		//AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(region).build();
		CompareFacesRequest compareFacesRequest = null;
		try (InputStream targetImgStream = new FileInputStream(targetImage)) {
			if (hasS3Photo) {
				System.out.println("s3");
				compareFacesRequest = new CompareFacesRequest().withSourceImage(new Image()
								.withS3Object(new S3Object()
										.withName(sourceImage).withBucket(bucketName)))
						.withTargetImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream)))).withSimilarityThreshold(70F);
			} else {
				System.out.println("local file system");
				compareFacesRequest = new CompareFacesRequest().withSourceImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(new FileInputStream(new File(sourceImage))))))
						.withTargetImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream)))).withSimilarityThreshold(70F);
			}

			CompareFacesResult result = rekognitionClient.compareFaces(compareFacesRequest);
			List<CompareFacesMatch> lists = result.getFaceMatches();
			event.eventLog("AWS Face Match - Response", null, convertListToJSONArrayString(lists));
			if (!lists.isEmpty()) {
				for (CompareFacesMatch label : lists) {
					log.info(label.getFace() + ": Similarity is " + label.getSimilarity().toString());
					return true;
				}
			} else {
				log.info("Faces Does not match");
				return false;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			log.error("Exception", e);
			return false;
		}
		return false;
	}
	

	private List<String> getDetectedText(File targetImage) {
		List<String> detectedText = new ArrayList<>();
		try (InputStream targetImgStream = new FileInputStream(targetImage)) {
			//BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
			AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(region).build();
					//.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();

			DetectTextRequest request = new DetectTextRequest()
					.withImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream))));
			DetectTextResult result = rekognitionClient.detectText(request);
			List<TextDetection> textDetections = result.getTextDetections();
			for (TextDetection text : textDetections) {
				log.info("Detected: " + text.getDetectedText());
				detectedText.add(text.getDetectedText());
			}
			event.eventLog("AWS-Text Extraction Response", null, String.join(",", detectedText));
		} catch (Exception e) {
			log.error("Exception", e);
			e.printStackTrace();
		}
		return detectedText;
	}

	private boolean hasDetectFacesinImage(File sourceImage ) throws IOException {
		try (InputStream targetImgStream = new FileInputStream(sourceImage)) {
			//BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
			AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(region).build();
					//.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
			DetectFacesRequest request = new DetectFacesRequest()
					.withImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream))))
					.withAttributes(Attribute.ALL);
			DetectFacesResult result = rekognitionClient.detectFaces(request);
			List < FaceDetail > faceDetails = result.getFaceDetails();
			event.eventLog("AWS Face Detection Response", null, convertFaceDetailListToJSONArrayString(faceDetails));
			return faceDetails.size()>0;

		} catch (Exception e) {
			log.error("Exception", e);
			return false;
		}
	}

	private String convertListToJSONArrayString(List<CompareFacesMatch> list){
		JsonArray array = new JsonArray();
		list.forEach(detail -> array.add(JsonObject.mapFrom(detail)));
		return array.toString();
	}

	private String convertFaceDetailListToJSONArrayString(List<FaceDetail> list){
		JsonArray array = new JsonArray();
		list.forEach(detail -> array.add(JsonObject.mapFrom(detail)));
		return array.toString();
	}
	public void storeApproveRequiredEmpPhoto(File faceImage,String csEmployeeId,boolean isHasS3Photo) throws IOException {
		if(isHasS3Photo){
			FormData photoFrontObj = FormData.builder()
					.data(faceImage)
					.filename(csEmployeeId)
					.mimetype("image/png")
					.build();
			s3.putObject(buildPutRequest(photoFrontObj), RequestBody.fromFile(faceImage));
		} else {
			File customDir = new File(uploadDir);
			if (!customDir.exists()) {
				customDir.mkdir();
			} else {
				String photoFrontFileName = customDir.getAbsolutePath() +
						File.separator + csEmployeeId + ".png";
				Files.deleteIfExists(new File(photoFrontFileName).toPath());
				Files.write(Paths.get(photoFrontFileName), Files.readAllBytes(faceImage.toPath()),
						StandardOpenOption.CREATE_NEW);
			}
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
		try {
			fileContent = s3.getObject(buildGetRequest(photoUrl)).readAllBytes();
			System.out.println(">>>>"+fileContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "data:image/png;base64,"+ Base64.getEncoder().encodeToString(fileContent);
	}

	/*private EmployeeRecognition getParseEmployeeObject(Employee employee) {
		EmployeeRecognition recognition = new EmployeeRecognition();
		recognition.setEmployeeId(employee.getId());
		recognition.setHasMatched(true);
		recognition.setStatus(StatusType.FULL_MATCH.getType());
		return recognition;
	}

	private VehicleRecognition getParseVehicleObject(Vehicle vehicle) {
		VehicleRecognition recognition = new VehicleRecognition();
		recognition.setEmployeeId(vehicle.getId());
		recognition.setHasMatched(true);
		recognition.setStatus(StatusType.FULL_MATCH.getType());
		return recognition;
	}*/
	/*private Employee getEmployeeMatchedRecord(File targetImage) {
		List<Employee> employees = Employee.listAll();
		return employees.stream().filter(emp -> {
			return hasFaceMatched(emp.isHasS3Photo(), emp.getPhotoFront(), targetImage);
		}).findFirst().get();
	}

	private Vehicle getVehicleMatchedRecord(File targetImage) {
		List<Vehicle> vehicles = Vehicle.listAll();
		return vehicles.stream().filter(veh -> {
			return hasVehicleMatched(veh.isHasS3Photo(), veh.getPhotoFront(), targetImage, veh.getRegNo());
		}).findFirst().get();
	}*/

	/*private boolean hasVehicleMatched(boolean hasS3Photo, String sourceImage, File targetImage, String regNo) {

		String detectedRegNo = getDetectedText(targetImage).stream().filter(text -> text.contains(regNo)).findFirst().orElse("");
		BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();

		try (InputStream targetImgStream = new FileInputStream(targetImage)) {
			List<TextDetection> textDetections = null;
			if (hasS3Photo) {
				System.out.println("s3");
				DetectTextRequest request = new DetectTextRequest()
						.withImage(new Image()
								.withS3Object(new S3Object()
										.withName(sourceImage)
										.withBucket(bucketName)));
				DetectTextResult result = rekognitionClient.detectText(request);
				textDetections = result.getTextDetections();
			} else {
				System.out.println("local");
				DetectTextRequest request = new DetectTextRequest()
						.withImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream))));
				DetectTextResult result = rekognitionClient.detectText(request);
				textDetections = result.getTextDetections();
			}

			System.out.println("Detected lines and words for " + sourceImage);
			for (TextDetection text : textDetections) {

				System.out.println("Detected: " + text.getDetectedText());
				System.out.println("Confidence: " + text.getConfidence().toString());
				System.out.println("Id : " + text.getId());
				System.out.println("Parent Id: " + text.getParentId());
				System.out.println("Type: " + text.getType());
				System.out.println();
				return (StringUtils.isNotBlank(detectedRegNo) && detectedRegNo.contains(text.getDetectedText()));
			}
		} catch (AmazonRekognitionException | IOException e) {
			e.printStackTrace();
		}

		return false;
	}*/
}
