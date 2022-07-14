package com.optimus.ats.dto;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import javax.ws.rs.core.MediaType;
import java.io.File;

public class EmployeeDto {

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private String employeeName;

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private String mobile;

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private String email;

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private boolean hasS3Photo;

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private String department;

	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private String designation;

	private String photoFront;
	private String photoIdCard;
	@RestForm("photoFrontFile")
	private File photoFrontFile;
	@RestForm("photoIDCardFile")
	private File photoIDCardFile;

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isHasS3Photo() {
		return hasS3Photo;
	}

	public void setHasS3Photo(boolean hasS3Photo) {
		this.hasS3Photo = hasS3Photo;
	}

	public String getPhotoFront() {
		return photoFront;
	}

	public void setPhotoFront(String photoFront) {
		this.photoFront = photoFront;
	}

	public String getPhotoIdCard() {
		return photoIdCard;
	}

	public void setPhotoIdCard(String photoIdCard) {
		this.photoIdCard = photoIdCard;
	}

	public File getPhotoFrontFile() {
		return photoFrontFile;
	}

	public void setPhotoFrontFile(File photoFrontFile) {
		this.photoFrontFile = photoFrontFile;
	}

	public File getPhotoIDCardFile() {
		return photoIDCardFile;
	}

	public void setPhotoIDCardFile(File photoIDCardFile) {
		this.photoIDCardFile = photoIDCardFile;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}
}
