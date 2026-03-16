package com.agms.sensor_telemetry_service.service.impl;

import com.agms.sensor_telemetry_service.dto.DeviceDTO;
import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;
import com.agms.sensor_telemetry_service.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorServiceImpl implements SensorService {

    private final RestTemplate restTemplate;

    @Value("${external.iot.base-url}")
    private String baseUrl;

    @Value("${external.iot.username}")
    private String username;

    @Value("${external.iot.password}")
    private String password;

    private String accessToken;
    private String refreshToken;

    @Override
    public void login() {
        String url = baseUrl + "/auth/login";
        Map<String, String> request = Map.of("username", username, "password", password);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                this.accessToken = (String) response.getBody().get("accessToken");
                this.refreshToken = (String) response.getBody().get("refreshToken");
                log.info("Successfully logged into External IoT API.");
            }
        } catch (Exception e) {
            log.error("Login failed for External IoT API: {}", e.getMessage());
        }
    }

    @Override
    public String getAccessToken() {
        if (this.accessToken == null) {
            login();
        }
        return this.accessToken;
    }

    @Override
    public void refreshAccessToken() {
        String url = baseUrl + "/auth/refresh";
        Map<String, String> request = Map.of("refreshToken", this.refreshToken);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                this.accessToken = (String) response.getBody().get("accessToken");
                log.info("Access Token refreshed.");
            }
        } catch (Exception e) {
            log.warn("Token refresh failed, attempting full login.");
            login();
        }
    }

    @Override
    public DeviceDTO registerDeviceAtExternalApi(DeviceDTO deviceDTO) {
        String url = baseUrl + "/devices";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DeviceDTO> entity = new HttpEntity<>(deviceDTO, headers);

        try {
            ResponseEntity<DeviceDTO> response = restTemplate.postForEntity(url, entity, DeviceDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Device registration failed: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public SensorTelemetryDTO fetchTelemetryFromExternal(String deviceId) {
        String url = baseUrl + "/devices/telemetry/" + deviceId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAccessToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, Object> valueMap = (Map<String, Object>) body.get("value");

                return new SensorTelemetryDTO(
                        (String) body.get("deviceId"),
                        (String) body.get("zoneId"),
                        Double.parseDouble(valueMap.get("temperature").toString()),
                        Double.parseDouble(valueMap.get("humidity").toString()),
                        Instant.parse((String) body.get("capturedAt"))
                );
            }
        } catch (Exception e) {
            if (e.getMessage().contains("401")) {
                refreshAccessToken();
                return fetchTelemetryFromExternal(deviceId); // Retry once after refresh
            }
            log.error("Failed to fetch telemetry for device {}: {}", deviceId, e.getMessage());
        }
        return null;
    }
}