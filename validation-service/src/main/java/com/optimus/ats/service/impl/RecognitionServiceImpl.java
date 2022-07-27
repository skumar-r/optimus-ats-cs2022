package com.optimus.ats.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optimus.ats.common.CommonResource;
import com.optimus.ats.common.ServiceResponse;
import com.optimus.ats.common.StatusType;
import com.optimus.ats.dto.RecognitionDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.model.EmployeeRecognition;
import com.optimus.ats.model.Vehicle;
import com.optimus.ats.model.VehicleRecognition;
import com.optimus.ats.service.RecognitionService;
import com.optimus.ats.service.ValidationService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class RecognitionServiceImpl extends CommonResource implements RecognitionService {

	@Inject
	ValidationService validationService;
	
	@ConfigProperty(name = "bucket.name")
	String bucketName;

	@ConfigProperty(name = "aws.accessKeyId")
	String ACCESS_KEY;
	@ConfigProperty(name = "aws.secretAccessKey")
	String SECRET_KEY;

	@ConfigProperty(name = "quarkus.s3.aws.region")
	String region;

	@Transactional
	@Override
	public ServiceResponse save(RecognitionDto dto) throws IOException {
		ServiceResponse response = ServiceResponse.createSuccessServiceResponse();
		response.getContentMap().put("StatusType",StatusType.NOT_FOUND.getType());
		try {
			if (dto != null && dto.getFormData().exists() &&  dto.getIdCardData().exists()) {
				if (StringUtils.equals("employee", dto.getType()) & hasDetectFacesinImage(dto.getFormData()) && hasDetectFacesinImage(dto.getIdCardData())) {
					return getEmployeeMatchedRecord(dto.getIdCardData(), dto.getFormData());
				} else{
					response.getContentMap().put("message","Uploaded image does not contain face.");
				}
			} else {
				response.getContentMap().put("message","Employee Photo and ID card required");
			}
		} catch (Exception e) {
			response.getContentMap().put("message","Employee Photo and ID card required");
			return response;
		}
		return response;
	}

	private ServiceResponse getEmployeeMatchedRecord(File idCardImage, File faceImage) throws IOException {
		ServiceResponse response = ServiceResponse.createSuccessServiceResponse();
		List<String> extractedText = getDetectedText(idCardImage);
		System.out.println("dd>>"+extractedText.size());
		String csId = extractedText.stream().filter(item -> item.contains("CS")).findFirst().orElse(null);
		System.out.println("csId>>"+csId);
		Employee employee = null;
		if (StringUtils.isNotBlank(csId)) {
			List<Employee> employees = Employee.listAll();
			employee = employees.stream().filter(empl -> StringUtils.equals(empl.getCsEmployeeId(), csId)).findFirst().get();

			if(Objects.isNull(employee)){
				/// not found
				response.getContentMap().put("StatusType",StatusType.NOT_FOUND.getType());
				response.getContentMap().put("message","Employee ID does not exists");
			} else{
				System.out.println("employee>>"+employee.getEmployeeName());
				System.out.println("employee photo>>"+employee.getPhotoFront());

				byte[] fileContent = FileUtils.readFileToByteArray(new File(employee.getPhotoFront()));
				String encodedString = Base64.getEncoder().encodeToString(fileContent);
				response.getContentMap().put("employee",employee);
				response.getContentMap().put("empPhoto",encodedString);
				if(hasFaceMatched(employee.isHasS3Photo(), employee.getPhotoFront(), faceImage)){
					// matched
					response.getContentMap().put("StatusType",StatusType.FULL_MATCH.getType());
				} else {
					// no match and call decision service
					validationService.invokeDecisionService(employee.getId(), null);
					response.getContentMap().put("StatusType",StatusType.NO_MATCH.getType());
				}
			}
		}
		return response;
	}



	private boolean hasFaceMatched(boolean hasS3Photo, String sourceImage, File targetImage) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();

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

			if (!lists.isEmpty()) {
				for (CompareFacesMatch label : lists) {
					System.out.println(label.getFace() + ": Similarity is " + label.getSimilarity().toString());
					return true;
				}
			} else {
				System.out.println("Faces Does not match");
				return false;
			}
		} catch (AmazonRekognitionException | IOException e) {
			//e.printStackTrace();
			return false;
		}
		return false;
	}

	private List<String> getDetectedText(File targetImage) {
		List<String> detectedText = new ArrayList<>();
		try (InputStream targetImgStream = new FileInputStream(targetImage)) {
			BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
			AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();

			DetectTextRequest request = new DetectTextRequest()
					.withImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream))));
			DetectTextResult result = rekognitionClient.detectText(request);
			List<TextDetection> textDetections = result.getTextDetections();
			for (TextDetection text : textDetections) {
				System.out.println("Detected: " + text.getDetectedText());
				detectedText.add(text.getDetectedText());
			}
		} catch (AmazonRekognitionException | IOException e) {
			e.printStackTrace();
		}
		return detectedText;
	}

	private boolean hasDetectFacesinImage(File sourceImage ) throws IOException {
		try (InputStream targetImgStream = new FileInputStream(sourceImage)) {
			BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
			AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
			DetectFacesRequest request = new DetectFacesRequest()
					.withImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream))))
					.withAttributes(Attribute.ALL);
			DetectFacesResult result = rekognitionClient.detectFaces(request);
			List < FaceDetail > faceDetails = result.getFaceDetails();
			return faceDetails.size()>0;

		} catch (AmazonRekognitionException |  FileNotFoundException e) {
			System.out.println(e.getMessage());
			return false;
		}
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
