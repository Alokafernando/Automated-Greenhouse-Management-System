package com.agms.sensor_telemetry_service.service;

import com.agms.sensor_telemetry_service.dto.DeviceDTO;
import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;

public interface SensorService {

    void login();

    String getAccessToken();

    void refreshAccessToken();

    DeviceDTO registerDeviceAtExternalApi(DeviceDTO deviceDTO);

    SensorTelemetryDTO fetchTelemetryFromExternal(String deviceId);
}