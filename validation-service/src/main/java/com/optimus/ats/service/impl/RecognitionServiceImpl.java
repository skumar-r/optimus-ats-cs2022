package com.optimus.ats.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.optimus.ats.common.CommonResource;
import com.optimus.ats.common.StatusType;
import com.optimus.ats.dto.RecognitionDto;
import com.optimus.ats.model.Employee;
import com.optimus.ats.model.EmployeeRecognition;
import com.optimus.ats.model.Vehicle;
import com.optimus.ats.model.VehicleRecognition;
import com.optimus.ats.service.RecognitionService;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.utils.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RecognitionServiceImpl extends CommonResource implements RecognitionService {

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
	public Response save(RecognitionDto dto) throws IOException {
		try {
			if (dto != null) {
				if (StringUtils.equals("employee", dto.getType())) {
					EmployeeRecognition.persist(getParseEmployeeObject(getEmployeeMatchedRecord(dto.getFormData())));
				} else {
					VehicleRecognition.persist(getParseVehicleObject(getVehicleMatchedRecord(dto.getFormData())));
				}
			}
		} catch (Exception e) {
			return Response.ok().status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok().status(Response.Status.CREATED).build();
	}

	private EmployeeRecognition getParseEmployeeObject(Employee employee) {
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
	}

	private Employee getEmployeeMatchedRecord(File targetImage) {
		List<Employee> employees = Employee.listAll();
		return employees.stream().filter(emp -> {
			return hasFaceMatched(emp.isHasS3Photo(), emp.getPhotoFront(), targetImage);
		}).findFirst().get();
	}
	private Vehicle getVehicleMatchedRecord(File targetImage) {
		List<Vehicle> vehicles = Vehicle.listAll();
		return vehicles.stream().filter(veh -> {
			return hasVehicleMatched(veh.isHasS3Photo(), veh.getPhotoFront(), targetImage,veh.getRegNo());
		}).findFirst().get();
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
			e.printStackTrace();
		}
		return false;
	}

	private boolean hasVehicleMatched(boolean hasS3Photo, String sourceImage, File targetImage, String regNo) {

		String detectedRegNo = getDetectedText(targetImage).stream().filter(text->text.contains(regNo)).findFirst().orElse("");
		BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();


		try (InputStream targetImgStream = new FileInputStream(targetImage)){
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
					return  (StringUtils.isNotBlank(detectedRegNo) && detectedRegNo.contains(text.getDetectedText()));

				}

		} catch(AmazonRekognitionException |  IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	private List<String> getDetectedText(File targetImage) {
		BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
		List<String> detectedText = new ArrayList<>();
		try (InputStream targetImgStream = new FileInputStream(targetImage)) {
			DetectTextRequest request = new DetectTextRequest()
					.withImage(new Image().withBytes(ByteBuffer.wrap(IOUtils.toByteArray(targetImgStream))));
			DetectTextResult result = rekognitionClient.detectText(request);
			List<TextDetection> textDetections = result.getTextDetections();
			for (TextDetection text : textDetections) {
				System.out.println("Detected: " + text.getDetectedText());
				detectedText.add(text.getDetectedText());
			}
		}catch(AmazonRekognitionException |  IOException e) {
			e.printStackTrace();
		}
		return detectedText;
	}
}
