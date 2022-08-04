package com.optimus.ats.dto;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import javax.ws.rs.core.MediaType;
import java.io.File;

public class RecognitionDto {
	@RestForm
	@PartType(MediaType.TEXT_PLAIN)
	private String type;

	@RestForm("resourceFile")
	private File formData;

	@RestForm("idCardFile")
	private File idCardData;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public File getFormData() {
		return formData;
	}

	public void setFormData(File formData) {
		this.formData = formData;
	}

	public File getIdCardData() {
		return idCardData;
	}

	public void setIdCardData(File idCardData) {
		this.idCardData = idCardData;
	}
}
