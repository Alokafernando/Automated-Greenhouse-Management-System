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

    private final ZoneClient zoneClient;
    private final SensorService sensorService;
    private final AutomationClient automationClient;

    public SensorFetcher(ZoneClient zoneClient, SensorService sensorService, AutomationClient automationClient) {
        this.zoneClient = zoneClient;
        this.sensorService = sensorService;
        this.automationClient = automationClient;
    }

    /**
     * initialDelay = 20000 (20 seconds) gives the Load Balancer
     * enough time to fetch the registry from Eureka before the first call.
     */
    @Scheduled(fixedRate = 10000, initialDelay = 20000)
    public void fetchAndPushTelemetry() {
        try {
            log.debug("Starting scheduled telemetry fetch...");

            // 1. Get all zones to find active device IDs
            List<ZoneDTO> zones = zoneClient.getAllZones();

            if (zones == null || zones.isEmpty()) {
                log.warn("No zones found to fetch telemetry for.");
                return;
            }

            for (ZoneDTO zone : zones) {
                if (zone.getDeviceId() != null) {
                    // 2. Fetch data from real IoT API
                    SensorTelemetryDTO data = sensorService.fetchTelemetryFromExternal(zone.getDeviceId());

                    if (data != null) {
                        // 3. Push to Automation Service
                        automationClient.processTelemetry(data);
                        log.info("Pushed data for Zone: {} (ID: {}) - Temp: {}°C",
                                zone.getName(), zone.getDeviceId(), data.getTemperature());
                    }
                }
            }
        } catch (feign.RetryableException e) {
            log.error("Target service (ZONE-SERVICE) is currently unavailable. Retrying in next cycle...");
        } catch (Exception e) {
            log.error("Scheduled Fetch Failed: {}", e.getMessage());
        }
    }
}