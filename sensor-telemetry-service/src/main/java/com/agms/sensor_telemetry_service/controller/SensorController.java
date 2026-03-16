package com.agms.sensor_telemetry_service.controller;

import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;
import com.agms.sensor_telemetry_service.service.impl.SensorServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorServiceImpl sensorService;

    public SensorController(SensorServiceImpl sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorTelemetryDTO> getLatest(@RequestParam String deviceId) {
        SensorTelemetryDTO data = sensorService.getLatestLocalData(deviceId);
        return data != null ? ResponseEntity.ok(data) : ResponseEntity.notFound().build();
    }
}