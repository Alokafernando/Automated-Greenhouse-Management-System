package com.agms.sensor_telemetry_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelemetryValue {
    private double temperature;
    private String tempUnit;
    private double humidity;
    private String humidityUnit;
}
