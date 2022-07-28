package com.optimus.ats.common;

public enum StatusType {
	FULL_MATCH("MATCHED"),
	APPROVAL_REQUIRED("APPROVAL REQUIRED"),
	NOT_FOUND("NOT FOUND"),
	NO_MATCH("NO MATCH"),
	ERROR("ERROR"),
	NO_FACE("NO FACE");
	
	final String type;

	StatusType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
