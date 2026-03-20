package com.agms.sensor_telemetry_service.service;

import com.agms.sensor_telemetry_service.dto.DeviceDTO;
import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;

import java.util.Map;

public interface SensorService {

    DeviceDTO registerDeviceAtExternalApi(DeviceDTO deviceDTO);

    DeviceDTO[] getAllDevices();

    String getAccessToken();

    void refreshAccessToken();
}