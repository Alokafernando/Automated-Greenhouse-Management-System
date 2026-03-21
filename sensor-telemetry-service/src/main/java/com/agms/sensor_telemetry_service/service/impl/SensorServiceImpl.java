package com.agms.sensor_telemetry_service.service.impl;

import com.agms.sensor_telemetry_service.dto.DeviceDTO;
import com.agms.sensor_telemetry_service.service.SensorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SensorServiceImpl implements SensorService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.iot.base-url}")
    private String iotBaseUrl;

    @Value("${external.iot.username}")
    private String iotUsername;

    @Value("${external.iot.password}")
    private String iotPassword;

    private String accessToken;
    private String refreshToken;

    private static final int MAX_RETRIES = 3;

    // ================= EXTERNAL IOT OPERATIONS =================

    @Override
    public DeviceDTO registerDeviceAtExternalApi(DeviceDTO deviceDTO) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                String url = iotBaseUrl + "/devices";
                String token = getAccessToken();

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<DeviceDTO> entity = new HttpEntity<>(deviceDTO, headers);
                ResponseEntity<DeviceDTO> response = restTemplate.postForEntity(
                        url, entity, DeviceDTO.class);

                log.info("✅ Device registered at IoT API: {}", response.getBody());
                return response.getBody();

            } catch (HttpClientErrorException.Unauthorized e) {
                attempts++;
                log.warn("401 from IoT API - refreshing token...");
                accessToken = null; // force re-login
                refreshAccessToken();
            } catch (Exception e) {
                attempts++;
                log.error("❌ Registration attempt {} failed: {}", attempts, e.getMessage());
            }
        }
        throw new RuntimeException("Failed to register device after retries");
    }

    @Override
    public DeviceDTO[] getAllDevices() {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                String url = iotBaseUrl + "/devices";
                String token = getAccessToken();

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<DeviceDTO[]> response = restTemplate.exchange(
                        url, HttpMethod.GET, entity, DeviceDTO[].class);
                return response.getBody();

            } catch (HttpClientErrorException.Unauthorized e) {
                attempts++;
                accessToken = null;
                refreshAccessToken();
            } catch (Exception e) {
                attempts++;
                log.error("❌ Fetch attempt failed: {}", e.getMessage());
            }
        }
        return new DeviceDTO[0];
    }

    // ================= IOT AUTH OPERATIONS =================

    @Override
    public String getAccessToken() {
        if (accessToken == null) {
            login();
        }
        return accessToken;
    }

    private void login() {
        // ✅ FIX: Login to IoT API directly — NOT local auth service
        // IoT API returns flat JSON: {"accessToken":"...", "refreshToken":"..."}
        // Local auth returns nested: {"data": {"accessToken":"..."}}
        String loginUrl = iotBaseUrl + "/auth/login";

        Map<String, String> request = new HashMap<>();
        request.put("username", iotUsername);  // buddhika
        request.put("password", iotPassword);  // 1234

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            log.info("🔐 Logging into IoT API: {}", loginUrl);
            ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                // ✅ IoT API returns flat — no "data" wrapper
                this.accessToken = (String) body.get("accessToken");
                this.refreshToken = (String) body.get("refreshToken");

                log.info("✅ IoT Login Success.");
            }
        } catch (Exception e) {
            log.error("❌ IoT Auth Failed: {}", e.getMessage());
            throw new RuntimeException("Could not authenticate with IoT API: " + e.getMessage());
        }
    }

    @Override
    public void refreshAccessToken() {
        // ✅ FIX: Refresh via IoT API
        String refreshUrl = iotBaseUrl + "/auth/refresh";

        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(refreshUrl, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                this.accessToken = (String) body.get("accessToken");
                log.info("🔄 IoT Token refreshed.");
            }
        } catch (Exception e) {
            log.warn("Refresh failed, re-logging into IoT API...");
            login();
        }
    }
}