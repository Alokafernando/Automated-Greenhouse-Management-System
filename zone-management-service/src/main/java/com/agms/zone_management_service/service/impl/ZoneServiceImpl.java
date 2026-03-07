package com.agms.zone_management_service.service.impl;

import com.agms.zone_management_service.client.IoTIntegrationClient;

import com.agms.zone_management_service.dto.ZoneDTO;
import com.agms.zone_management_service.entity.Zone;
import com.agms.zone_management_service.repository.ZoneRepository;
import com.agms.zone_management_service.service.ZoneService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final IoTIntegrationClient iotClient;

    public ZoneServiceImpl(ZoneRepository zoneRepository,
                           IoTIntegrationClient iotClient) {
        this.zoneRepository = zoneRepository;
        this.iotClient = iotClient;
    }

    @Override
    public Zone createZone(ZoneDTO dto) {

        if(dto.getMinTemp() >= dto.getMaxTemp()){
            throw new RuntimeException("minTemp must be less than maxTemp");
        }

        Map<String,String> response = iotClient.registerDevice();
        String deviceId = response.get("deviceId");

        Zone zone = new Zone();

        zone.setName(dto.getName());
        zone.setMinTemp(dto.getMinTemp());
        zone.setMaxTemp(dto.getMaxTemp());
        zone.setMinHumidity(dto.getMinHumidity());
        zone.setMaxHumidity(dto.getMaxHumidity());
        zone.setDeviceId(deviceId);

        return zoneRepository.save(zone);
    }

    @Override
    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
    }

    @Override
    public Zone updateZone(Long id, ZoneDTO dto) {

        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        if(dto.getMinTemp() >= dto.getMaxTemp()){
            throw new RuntimeException("minTemp must be less than maxTemp");
        }

        zone.setMinTemp(dto.getMinTemp());
        zone.setMaxTemp(dto.getMaxTemp());
        zone.setMinHumidity(dto.getMinHumidity());
        zone.setMaxHumidity(dto.getMaxHumidity());

        return zoneRepository.save(zone);
    }

    @Override
    public void deleteZone(Long id) {
        zoneRepository.deleteById(id);
    }
}