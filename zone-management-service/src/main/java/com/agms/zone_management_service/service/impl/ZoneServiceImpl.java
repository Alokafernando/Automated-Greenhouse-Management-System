package com.agms.zone_management_service.service.impl;

import com.agms.zone_management_service.client.SensorClient;
import com.agms.zone_management_service.dto.ZoneDTO;
import com.agms.zone_management_service.entity.Zone;
import com.agms.zone_management_service.repository.ZoneRepository;
import com.agms.zone_management_service.service.ZoneService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final SensorClient sensorClient;
    private final ModelMapper modelMapper;

    public ZoneServiceImpl(ZoneRepository zoneRepository,
                           SensorClient sensorClient,
                           ModelMapper modelMapper) {
        this.zoneRepository = zoneRepository;
        this.sensorClient = sensorClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public Zone createZone(ZoneDTO zoneDTO) {

        if (zoneDTO.getName() == null || zoneDTO.getName().isEmpty())
            throw new IllegalArgumentException("Zone name cannot be null or empty");

        if (zoneDTO.getMinTemp() >= zoneDTO.getMaxTemp())
            throw new IllegalArgumentException("minTemp must be less than maxTemp");

        String zoneId = UUID.randomUUID().toString();

        Map<String, Object> deviceRequest = new HashMap<>();
        deviceRequest.put("name", zoneDTO.getName() + "-Sensor");
        deviceRequest.put("zoneId", zoneId);

        Map<String, Object> deviceResponse = sensorClient.registerDevice(deviceRequest);

        zoneDTO.setDeviceId((String) deviceResponse.get("deviceId"));
        zoneDTO.setUserId((String) deviceResponse.get("userId"));

        Zone zone = modelMapper.map(zoneDTO, Zone.class);
        return zoneRepository.save(zone);
    }

    @Override
    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found with ID: " + id));
    }

    @Override
    public Zone updateZone(Long id, ZoneDTO zoneDTO) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found with ID: " + id));

        if (zoneDTO.getMinTemp() >= zoneDTO.getMaxTemp())
            throw new IllegalArgumentException("minTemp must be less than maxTemp");

        zone.setName(zoneDTO.getName());
        zone.setMinTemp(zoneDTO.getMinTemp());
        zone.setMaxTemp(zoneDTO.getMaxTemp());
        zone.setDeviceId(zoneDTO.getDeviceId());

        return zoneRepository.save(zone);
    }

    @Override
    public void deleteZone(Long id) {
        if (!zoneRepository.existsById(id))
            throw new RuntimeException("Zone not found with ID: " + id);

        zoneRepository.deleteById(id);
    }

    @Override
    public List<ZoneDTO> findAll() {
        return zoneRepository.findAll().stream()
                .map(zone -> modelMapper.map(zone, ZoneDTO.class))
                .toList();
    }
}