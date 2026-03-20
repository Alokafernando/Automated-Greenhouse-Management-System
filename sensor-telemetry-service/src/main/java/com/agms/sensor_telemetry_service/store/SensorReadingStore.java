package com.agms.sensor_telemetry_service.store;

import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SensorReadingStore {

    private final Map<String, SensorTelemetryDTO> latestReadings = new ConcurrentHashMap<>();

    public void updateReading(String zoneId, SensorTelemetryDTO data) {
        latestReadings.put(zoneId, data);
    }

    public Map<String, SensorTelemetryDTO> getAllReadings() {
        return Collections.unmodifiableMap(latestReadings);
    }
}