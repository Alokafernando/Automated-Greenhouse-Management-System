package com.agms.zone_management_service.service;

import com.agms.zone_management_service.dto.ZoneDTO;
import com.agms.zone_management_service.dto.ZoneResponseDTO;

import java.util.List;

public interface ZoneService {

    ZoneResponseDTO createZone(ZoneDTO dto);

    ZoneResponseDTO getZone(Long id);

    ZoneResponseDTO updateZone(Long id, ZoneDTO dto);

    void deleteZone(Long id);

    List<ZoneResponseDTO> getAllZones();
}