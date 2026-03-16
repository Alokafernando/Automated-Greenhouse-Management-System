package com.agms.sensor_telemetry_service.service.impl;

import com.agms.sensor_telemetry_service.dto.DeviceDTO;
import com.agms.sensor_telemetry_service.dto.SensorTelemetryDTO;
import com.agms.sensor_telemetry_service.service.SensorService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SensorServiceImpl implements SensorService {

    private final RestTemplate restTemplate;
    private String cachedToken;

    private final Map<String, SensorTelemetryDTO> latestReadings = new ConcurrentHashMap<>();

    @Value("${external.iot.base-url:http://104.211.95.241:8080/api}")
    private String baseUrl;

    @Value("${external.iot.username:root}")
    private String username;

    @Value("${external.iot.password:1234}")
    private String password;

    public SensorServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        login(); // Get token on startup
    }

    @Override
    public void login() {
        try {
            String loginUrl = baseUrl + "/auth/login";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", username);
            credentials.put("password", password);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(credentials, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                this.cachedToken = (String) response.getBody().get("token");
                log.info("Successfully authenticated with External IoT API");
            }
        } catch (Exception e) {
            log.error("Failed to login to External API: {}", e.getMessage());
        }
    }

    @Override
    public String getAccessToken() {
        return this.cachedToken;
    }

    @Override
    public void refreshAccessToken() {
        login();
    }

    @Override
    public SensorTelemetryDTO fetchTelemetryFromExternal(String deviceId) {
        try {
            String url = baseUrl + "/telemetry/" + deviceId;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getAccessToken());
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<SensorTelemetryDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, SensorTelemetryDTO.class);

            if (response.getBody() != null) {
                latestReadings.put(deviceId, response.getBody());
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("Error fetching telemetry for device {}: {}", deviceId, e.getMessage());
            if (e.getMessage().contains("401")) refreshAccessToken();
        }
        return null;
    }

    public SensorTelemetryDTO getLatestLocalData(String deviceId) {
        return latestReadings.get(deviceId);
    }

    @Override
    public DeviceDTO registerDeviceAtExternalApi(DeviceDTO deviceDTO) {
        return null;
    }
}