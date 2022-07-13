package com.optimus.ats.service;

import com.optimus.ats.dto.EmployeeDto;
import com.optimus.ats.dto.VehicleDto;

import javax.ws.rs.core.Response;
import java.io.IOException;

public interface VehicleService {
	Response saveVehicle(VehicleDto employee) throws IOException;
}
