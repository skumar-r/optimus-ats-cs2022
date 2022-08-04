package com.optimus.ats.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity()
@Cacheable
@Table(name = "T_VEHICLE")
public class Vehicle extends PanacheEntityBase {
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	@Column(name = "VEHICLE_ID", unique = true)
	private Long id;

	@Column(name = "REG_NO")
	private String regNo;

	@Column(name = "VEHICLE_DETAILS")
	private String vehicleDetails;

	@Column(name = "EMPLOYEE_ID", unique = true)
	private Long employeeId;

	@Column(name = "VEHICLE_PHOTO_FRONT")
	private String photoFront;

	@Column(name = "IS_S3PHOTO")
	private boolean hasS3Photo;

	@Transient
	private String vehiclePhoto;

	@Transient
	private String emplyeeName;

	@Transient
	private String csEmployeeId;

	public static Vehicle findByName(String name){
		return find("regNo", name).firstResult();
	}
	public static Vehicle findByEmployeeId(Long employeeId){
		return find("employeeId", employeeId).firstResult();
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getPhotoFront() {
		return photoFront;
	}

	public void setPhotoFront(String photoFront) {
		this.photoFront = photoFront;
	}

	public boolean isHasS3Photo() {
		return hasS3Photo;
	}

	public void setHasS3Photo(boolean hasS3Photo) {
		this.hasS3Photo = hasS3Photo;
	}

	public String getVehiclePhoto() {
		return vehiclePhoto;
	}

	public void setVehiclePhoto(String vehiclePhoto) {
		this.vehiclePhoto = vehiclePhoto;
	}

	public String getEmplyeeName() {
		return emplyeeName;
	}

	public void setEmplyeeName(String emplyeeName) {
		this.emplyeeName = emplyeeName;
	}

	public String getCsEmployeeId() {
		return csEmployeeId;
	}

	public void setCsEmployeeId(String csEmployeeId) {
		this.csEmployeeId = csEmployeeId;
	}
}
