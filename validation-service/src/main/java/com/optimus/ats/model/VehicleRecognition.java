package com.optimus.ats.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity()
@Cacheable
@Table(name = "T_VEHICLE_ENTRY")
public class VehicleRecognition extends PanacheEntityBase {
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	@Column(name = "VEHICLE_ENTRY_ID", unique = true)
	private Long id;

	@Column(name = "EMPLOYEE_ID")
	private Long employeeId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "MATCHED")
	private boolean hasMatched;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isHasMatched() {
		return hasMatched;
	}

	public void setHasMatched(boolean hasMatched) {
		this.hasMatched = hasMatched;
	}
}
