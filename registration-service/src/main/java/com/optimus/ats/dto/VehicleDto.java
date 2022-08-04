package com.optimus.ats.dto;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import javax.ws.rs.core.MediaType;
import java.io.File;

public class VehicleDto {

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private String regNo;

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private String vehicleDetails;

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private Long employeeId;

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private boolean hasS3Photo;

	@RestForm("photoFrontFile")
	private File photoFrontFile;

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getVehicleDetails() {
		return vehicleDetails;
	}

	public void setVehicleDetails(String vehicleDetails) {
		this.vehicleDetails = vehicleDetails;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public boolean isHasS3Photo() {
		return hasS3Photo;
	}

	public void setHasS3Photo(boolean hasS3Photo) {
		this.hasS3Photo = hasS3Photo;
	}

	public File getPhotoFrontFile() {
		return photoFrontFile;
	}

	public void setPhotoFrontFile(File photoFrontFile) {
		this.photoFrontFile = photoFrontFile;
	}
}
