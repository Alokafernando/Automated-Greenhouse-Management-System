package com.agms.sensor_telemetry_service.controller;

import com.agms.sensor_telemetry_service.dto.DeviceDTO;
import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;
import com.agms.sensor_telemetry_service.service.SensorService;
import com.agms.sensor_telemetry_service.store.SensorReadingStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    // FIX: Inject the dedicated store component instead of managing state here
    private final SensorReadingStore sensorReadingStore;

    public SensorController(SensorService sensorService, SensorReadingStore sensorReadingStore) {
        this.sensorService = sensorService;
        this.sensorReadingStore = sensorReadingStore;
    }

    @GetMapping("/latest")
    public ResponseEntity<Map<String, SensorTelemetryDTO>> getLatest() {
        return ResponseEntity.ok(sensorReadingStore.getAllReadings());
    }

    @PostMapping("/register")
    public ResponseEntity<DeviceDTO> registerDevice(@RequestBody DeviceDTO deviceDTO) {
        DeviceDTO registeredDevice = sensorService.registerDeviceAtExternalApi(deviceDTO);
        return ResponseEntity.ok(registeredDevice);
    }

    @GetMapping
    public ResponseEntity<DeviceDTO[]> getAllDevices() {
        DeviceDTO[] devices = sensorService.getAllDevices();
        return ResponseEntity.ok(devices);
    }
}