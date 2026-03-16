package com.agms.automation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorDataDTO {
    private String sensorId;
    private String zoneId;
    private String sensorType;
    private double value;
    private String unit;
    private Long timestamp;
}
