package com.optimus.ats.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity()
@Cacheable
@Table(name = "T_EMPLOYEE")
public class Employee extends PanacheEntityBase {
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	@Column(name = "EMPLOYEE_ID", unique = true)
	private Long id;

	@Column(name = "EMPLOYEE_NAME")
	private String employeeName;

	@Column(name = "MOBILE")
	private String mobile;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHOTO_FRONT")
	private String photoFront;

	@Column(name = "PHOTO_ID")
	private String photoIdcard;

	@Column(name = "IS_S3PHOTO")
	private boolean hasS3Photo;

	@Column(name = "DEPARTMENT")
	private String department;

	@Column(name = "DESIGNATION")
	private String designation;

	@Column(name = "CS_EMPLOYEE_ID")
	private String csEmployeeId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getPhotoFront() {
		return photoFront;
	}

	public void setPhotoFront(String photoFront) {
		this.photoFront = photoFront;
	}

	public String getPhotoIdcard() {
		return photoIdcard;
	}

	public void setPhotoIdcard(String photoIdcard) {
		this.photoIdcard = photoIdcard;
	}

	public boolean isHasS3Photo() {
		return hasS3Photo;
	}

	public void setHasS3Photo(boolean hasS3Photo) {
		this.hasS3Photo = hasS3Photo;
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

	public String getCsEmployeeId() {
		return csEmployeeId;
	}

	public void setCsEmployeeId(String csEmployeeId) {
		this.csEmployeeId = csEmployeeId;
	}
}
