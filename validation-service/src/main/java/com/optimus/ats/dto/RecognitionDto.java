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
}
