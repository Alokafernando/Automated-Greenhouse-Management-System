package com.agms.sensor_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorTelemetryDTO {

    private String deviceId;
    private String zoneId;
    private double temperature;
    private double humidity;
    private Instant capturedAt;
}