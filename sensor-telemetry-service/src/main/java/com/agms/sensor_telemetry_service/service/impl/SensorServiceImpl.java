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

    // ── External IoT API ──────────────────────────────
    @Value("${external.iot.base-url}")
    private String iotBaseUrl;

    @Value("${external.iot.username}")
    private String iotUsername;        // buddhika

    @Value("${external.iot.password}")
    private String iotPassword;        // 1234

    // ── Local Auth Service ────────────────────────────
    @Value("${auth.service.base-url}")
    private String authBaseUrl;

    // ✅ FIX: Separate credentials for local auth service
    @Value("${auth.service.username}")
    private String authUsername;       // buddhika (local)

    @Value("${auth.service.password}")
    private String authPassword;       // 1234 (local)

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
                ResponseEntity<DeviceDTO> response = restTemplate.postForEntity(url, entity, DeviceDTO.class);

                log.info("✅ Device registered at IoT API: {}", response.getBody());
                return response.getBody();

            } catch (HttpClientErrorException.Unauthorized e) {
                attempts++;
                log.warn("401 from IoT API - refreshing token...");
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
                refreshAccessToken();
            } catch (Exception e) {
                attempts++;
                log.error("❌ Fetch attempt failed: {}", e.getMessage());
            }
        }
        return new DeviceDTO[0];
    }

    // ================= LOCAL AUTH OPERATIONS =================

    @Override
    public String getAccessToken() {
        if (accessToken == null) {
            login();
        }
        return accessToken;
    }

    private void login() {
        String loginUrl = authBaseUrl + "/login";
        Map<String, String> request = new HashMap<>();

        // ✅ FIX: Use LOCAL auth credentials, not IoT credentials
        request.put("username", authUsername);
        request.put("password", authPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            log.info("Logging into Local Auth Service: {}", loginUrl);
            ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, String> tokenData = (Map<String, String>) body.get("data");

                this.accessToken = tokenData.get("accessToken");
                this.refreshToken = tokenData.get("refreshToken");
                log.info("✅ Login Success.");
            }
        } catch (Exception e) {
            log.error("❌ Auth Failed: {}", e.getMessage());
            throw new RuntimeException("Could not authenticate with Local Auth Service");
        }
    }

    @Override
    public void refreshAccessToken() {
        String refreshUrl = authBaseUrl + "/refresh";
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(refreshUrl, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Map<String, String> tokenData = (Map<String, String>) body.get("data");
                this.accessToken = tokenData.get("accessToken");
                log.info("🔄 Token refreshed.");
            }
        } catch (Exception e) {
            log.warn("Refresh failed, re-logging...");
            login();
        }
    }
}