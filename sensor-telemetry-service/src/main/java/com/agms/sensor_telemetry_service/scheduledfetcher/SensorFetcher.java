package com.agms.sensor_telemetry_service.scheduledfetcher;

import com.agms.sensor_telemetry_service.client.AutomationClient;
import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;
import com.agms.sensor_telemetry_service.dto.ZoneDTO;
import com.agms.sensor_telemetry_service.service.SensorService;
import com.agms.sensor_telemetry_service.store.SensorReadingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class SensorFetcher {

    @Autowired
    private SensorService authService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SensorReadingStore sensorReadingStore;

    @Autowired
    private AutomationClient automationClient;

    @Value("${external.iot.base-url}")
    private String baseUrl;

    // ✅ FIX: Direct URL — Feign/Eureka load balancer failing in Docker setup
    @Value("${zone.service.base-url}")
    private String zoneServiceUrl;

    @Scheduled(fixedRate = 10000)
    public void fetch() {
        // ✅ FIX: Use RestTemplate directly instead of ZoneClient Feign
        List<ZoneDTO> zones;
        try {
            ResponseEntity<ZoneDTO[]> response = restTemplate.getForEntity(
                    zoneServiceUrl + "/api/zones", ZoneDTO[].class);
            zones = response.getBody() != null
                    ? Arrays.asList(response.getBody())
                    : Collections.emptyList();
        } catch (Exception e) {
            log.error("Could not fetch zones from zone-service: {}", e.getMessage());
            return;
        }

        if (zones == null || zones.isEmpty()) {
            log.warn("No zones returned from zone-service. Skipping fetch cycle.");
            return;
        }

        String token = authService.getAccessToken();
        if (token == null) {
            log.error("Access token is null. Skipping fetch cycle.");
            return;
        }

        for (ZoneDTO zone : zones) {
            String deviceId = zone.getDeviceId();

            if (deviceId == null || deviceId.isBlank()) {
                log.warn("Zone '{}' (id={}) has no deviceId — skipping.", zone.getName(), zone.getId());
                continue;
            }

            String url = baseUrl + "/devices/telemetry/" + deviceId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            try {
                ResponseEntity<SensorTelemetryDTO> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, SensorTelemetryDTO.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    SensorTelemetryDTO data = response.getBody();

                    if (data == null) {
                        log.warn("Empty telemetry body for device '{}' in zone '{}'", deviceId, zone.getName());
                        continue;
                    }

                    String zoneIdStr = String.valueOf(zone.getId());
                    data.setZoneId(zoneIdStr);
                    sensorReadingStore.updateReading(zoneIdStr, data);

                    try {
                        automationClient.sendToAutomation(data);
                    } catch (Exception automationEx) {
                        log.error("Automation send failed for zone '{}': {}",
                                zone.getName(), automationEx.getMessage());
                    }

                    log.info("✅ Zone: {} | Temp: {}°{} | Humidity: {}{}",
                            zone.getName(),
                            data.getValue().getTemperature(),
                            data.getValue().getTempUnit(),
                            data.getValue().getHumidity(),
                            data.getValue().getHumidityUnit());
                }

            } catch (HttpClientErrorException.Unauthorized e) {
                log.warn("Token expired for zone '{}'. Refreshing...", zone.getName());
                authService.refreshAccessToken();

            } catch (HttpClientErrorException e) {
                log.error("HTTP {} error fetching telemetry for device '{}': {}",
                        e.getStatusCode(), deviceId, e.getMessage());

            } catch (Exception e) {
                log.error("Unexpected error fetching telemetry for device '{}' in zone '{}': {}",
                        deviceId, zone.getName(), e.getMessage());
            }
        }
    }
}