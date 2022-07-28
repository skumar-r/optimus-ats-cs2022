package com.optimus.ats.service;

import com.optimus.ats.common.ServiceResponse;
import com.optimus.ats.dto.RecognitionDto;

import javax.ws.rs.core.Response;
import java.io.IOException;

public interface RecognitionService {
	ServiceResponse validateEmployee(RecognitionDto dto) throws IOException;
}
