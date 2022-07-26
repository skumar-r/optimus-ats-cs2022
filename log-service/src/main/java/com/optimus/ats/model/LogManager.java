package com.optimus.ats.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity()
@Cacheable
@Table(name = "T_LOG")
public class LogManager extends PanacheEntityBase {
	@Id
	@GeneratedValue(strategy =GenerationType.AUTO)
	@Column(name = "LOG_ID", unique = true)
	private Long id;

	@Column(name = "SERVICE_NAME")
	private String serviceName;

	@Column(name = "DETAILS")
	private String details;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
}
