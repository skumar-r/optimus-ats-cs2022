package com.optimus.ats.service;

import com.optimus.ats.common.ServiceResponse;
import com.optimus.ats.dto.RecognitionDto;

import java.io.IOException;

public interface RecognitionService {
	ServiceResponse validateEmployee(RecognitionDto dto) throws IOException;
	ServiceResponse validateVehicle(RecognitionDto dto) throws IOException;
}
