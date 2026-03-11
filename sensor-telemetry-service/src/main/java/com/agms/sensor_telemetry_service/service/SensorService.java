package com.agms.sensor_telemetry_service.service;

import com.agms.sensor_telemetry_service.dto.DeviceDTO;
import com.agms.sensor_telemetry_service.dto.ZoneDTO;

import java.util.List;

public interface SensorService {

    void processTelemetry(com.agms.sensor_service.dto.SensorTelemetryDTO telemetryDTO);

    List<ZoneDTO> getAllZones();
}