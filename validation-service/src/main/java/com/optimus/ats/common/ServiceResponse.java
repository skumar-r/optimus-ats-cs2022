package com.optimus.ats.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jboss.resteasy.reactive.common.jaxrs.ResponseImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * If there are no existing generic response object, then we can use this to send response to client.
 * <p/>
 * A typical use of this object is to send a warning message to client based on validation results
 */

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ServiceResponse extends ResponseImpl {

	private ServiceResponse() {
	}

	private boolean success = false;
	private Map<String, Object> contentMap = new HashMap<>();

	@JsonProperty
	public Map<String, Object> getContentMap() {
		return contentMap;
	}

	@JsonProperty
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public static ServiceResponse createSuccessServiceResponse() {
		ServiceResponse newServiceResponse = new ServiceResponse();
		newServiceResponse.setSuccess(true);
		return newServiceResponse;
	}
}

