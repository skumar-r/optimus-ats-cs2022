package com.optimus.ats.service;

import com.optimus.ats.common.ServiceResponse;
import com.optimus.ats.dto.VehicleDto;

import java.io.IOException;

public interface VehicleService {
	ServiceResponse saveVehicle(VehicleDto employee) throws IOException;
}
