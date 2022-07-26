package com.optimus.ats.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LogDto {
	private String serviceName;
	private String details;
}
