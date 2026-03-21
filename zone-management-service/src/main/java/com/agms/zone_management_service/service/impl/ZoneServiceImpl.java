package com.agms.zone_management_service.service.impl;

import com.agms.zone_management_service.dto.DeviceRequestDTO;
import com.agms.zone_management_service.dto.DeviceResponseDTO;
import com.agms.zone_management_service.dto.ZoneDTO;
import com.agms.zone_management_service.dto.ZoneResponseDTO;
import com.agms.zone_management_service.entity.Zone;
import com.agms.zone_management_service.repository.ZoneRepository;
import com.agms.zone_management_service.service.ZoneService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ZoneServiceImpl implements ZoneService {

    @Autowired
    private ZoneRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${external.iot.base-url}")
    private String baseUrl;         // http://104.211.95.241:8080/api

    @Value("${auth.service.base-url}")
    private String authBaseUrl;     // http://localhost:8085/api

    @Value("${external.iot.username}")
    private String iotUsername;

    @Value("${external.iot.password}")
    private String iotPassword;

    @Override
    public ZoneResponseDTO createZone(ZoneDTO dto) {
        if (dto.getMinTemp() >= dto.getMaxTemp()) {
            throw new RuntimeException("Invalid temperature range: minTemp must be less than maxTemp");
        }

        if (dto.getDeviceId() == null || dto.getDeviceId().isBlank()) {
            throw new RuntimeException("deviceId is required (IoT API unavailable)");
        }

        Zone zone = Zone.builder()
                .name(dto.getName())
                .minTemp(dto.getMinTemp())
                .maxTemp(dto.getMaxTemp())
                .minHumidity(dto.getMinHumidity())
                .maxHumidity(dto.getMaxHumidity())
                .deviceId(dto.getDeviceId())
                .build();

        Zone saved = repository.save(zone);
        log.info("✅ Zone created: {} with deviceId: {}", saved.getName(), saved.getDeviceId());
        return toResponseDTO(saved);
    }

    @Override
    public ZoneResponseDTO getZone(Long id) {
        Zone zone = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found: " + id));
        return toResponseDTO(zone);
    }

    @Override
    public ZoneResponseDTO updateZone(Long id, ZoneDTO dto) {
        Zone zone = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found: " + id));

        if (dto.getMinTemp() >= dto.getMaxTemp()) {
            throw new RuntimeException("Invalid temperature range");
        }

        zone.setName(dto.getName());
        zone.setMinTemp(dto.getMinTemp());
        zone.setMaxTemp(dto.getMaxTemp());

        if (dto.getDeviceId() != null && !dto.getDeviceId().isBlank()) {
            zone.setDeviceId(dto.getDeviceId());
        }

        return toResponseDTO(repository.save(zone));
    }

    @Override
    public void deleteZone(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ZoneResponseDTO> getAllZones() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private String getIoTToken() {
        return fetchToken(baseUrl + "/auth/login", iotUsername, iotPassword);
    }

    private String getLocalToken() {
        return fetchToken(authBaseUrl + "/auth/login", "root", "1234");
    }

    private String fetchToken(String loginUrl, String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());

                JsonNode tokenNode = root.path("data").path("accessToken");
                if (tokenNode.isMissingNode()) {
                    tokenNode = root.path("accessToken");
                }

                if (tokenNode.isMissingNode() || tokenNode.isNull()) {
                    throw new RuntimeException("accessToken not found in response from: " + loginUrl);
                }

                log.info("✅ Login successful: {}", loginUrl);
                return tokenNode.asText();
            } else {
                throw new RuntimeException("Login returned status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("❌ Login failed [{}]: {}", loginUrl, e.getMessage());
            throw new RuntimeException("Could not authenticate at: " + loginUrl, e);
        }
    }

    private ZoneResponseDTO toResponseDTO(Zone zone) {
        return new ZoneResponseDTO(
                zone.getId(),
                zone.getName(),
                zone.getMinTemp(),
                zone.getMaxTemp(),
                zone.getMinHumidity(),
                zone.getMaxHumidity(),
                zone.getDeviceId()
        );
    }
}