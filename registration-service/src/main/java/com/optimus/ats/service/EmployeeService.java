package com.optimus.ats.service;

import com.optimus.ats.common.ServiceResponse;
import com.optimus.ats.dto.EmployeeDto;

import javax.ws.rs.core.Response;
import java.io.IOException;

public interface EmployeeService {
	ServiceResponse saveEmployee(EmployeeDto employee) throws IOException;
}
