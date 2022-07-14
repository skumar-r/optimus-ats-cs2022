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

	@Column(name = "VEHICLE_PHOTO_REAR")
	private String photoRear;

	@Column(name = "IS_S3PHOTO")
	private boolean hasS3Photo;


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

	public String getPhotoRear() {
		return photoRear;
	}

	public void setPhotoRear(String photoRear) {
		this.photoRear = photoRear;
	}

	public boolean isHasS3Photo() {
		return hasS3Photo;
	}

	public void setHasS3Photo(boolean hasS3Photo) {
		this.hasS3Photo = hasS3Photo;
	}

}
