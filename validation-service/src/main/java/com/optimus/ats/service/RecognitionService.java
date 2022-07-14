package com.optimus.ats.service;

import com.optimus.ats.dto.RecognitionDto;

import javax.ws.rs.core.Response;
import java.io.IOException;

public interface RecognitionService {
	Response save(RecognitionDto dto) throws IOException;
}
