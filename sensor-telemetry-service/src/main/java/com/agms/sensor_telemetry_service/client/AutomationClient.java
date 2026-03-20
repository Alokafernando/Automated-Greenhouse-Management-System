package com.agms.sensor_telemetry_service.client;

import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "automation-service") // Discovered via Eureka
public interface AutomationClient {
    
    @PostMapping("/api/automation/process")
    void sendToAutomation(@RequestBody SensorTelemetryDTO data);
}