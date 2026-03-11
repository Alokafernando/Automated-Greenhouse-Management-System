package com.agms.zone_management_service.service;

import com.agms.zone_management_service.dto.ZoneDTO;
import com.agms.zone_management_service.entity.Zone;

import java.util.List;

public interface ZoneService {

    Zone createZone(ZoneDTO zoneRequestDTO);

    Zone getZoneById(Long id);

    Zone updateZone(Long id, ZoneDTO zoneRequestDTO);

    void deleteZone(Long id);

    List<ZoneDTO> findAll();

}