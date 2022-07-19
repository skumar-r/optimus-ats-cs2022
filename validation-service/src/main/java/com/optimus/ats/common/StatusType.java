package com.optimus.ats.common;

public enum StatusType {
	FULL_MATCH("FULLMATCH"),
	APPROVAL_REQUIRED("APPROVALREQUIRED"),
	NO_MATCH("NOMATCH");

	final String type;

	StatusType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
