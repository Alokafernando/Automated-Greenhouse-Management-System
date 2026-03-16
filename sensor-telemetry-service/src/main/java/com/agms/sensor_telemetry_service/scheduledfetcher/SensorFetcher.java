package com.agms.sensor_telemetry_service.scheduledfetcher;

import com.agms.sensor_telemetry_service.client.AutomationClient;
import com.agms.sensor_telemetry_service.client.ZoneClient;
import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;
import com.agms.sensor_telemetry_service.dto.ZoneDTO;
import com.agms.sensor_telemetry_service.service.SensorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SensorFetcher {

    private final ZoneClient zoneClient; // Renamed Feign Client
    private final SensorService sensorService;
    private final AutomationClient automationClient;

    public SensorFetcher(ZoneClient zoneClient, SensorService sensorService, AutomationClient automationClient) {
        this.zoneClient = zoneClient;
        this.sensorService = sensorService;
        this.automationClient = automationClient;
    }

    @Scheduled(fixedRate = 10000)
    public void fetchAndPushTelemetry() {
        try {
            // 1. Get all zones to find active device IDs
            List<ZoneDTO> zones = zoneClient.getAllZones();

            for (ZoneDTO zone : zones) {
                if (zone.getDeviceId() != null) {
                    // 2. Fetch data from real IoT API via our Service
                    SensorTelemetryDTO data = sensorService.fetchTelemetryFromExternal(zone.getDeviceId());

                    if (data != null) {
                        // 3. Push to Automation Service
                        automationClient.processTelemetry(data);
                        log.info("Pushed data for Zone: {} - Temp: {}", zone.getName(), data.getTemperature());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Scheduled Fetch Failed: " + e.getMessage());
        }
    }
}