package com.agms.sensor_telemetry_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private int status;
    private String message;
    private Object data; // This will hold the Map of tokens
}
