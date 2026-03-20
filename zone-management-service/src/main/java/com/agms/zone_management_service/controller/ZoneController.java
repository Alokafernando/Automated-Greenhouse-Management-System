package com.agms.zone_management_service.controller;

import com.agms.zone_management_service.dto.DeviceResponseDTO;
import com.agms.zone_management_service.dto.ZoneDTO;
import com.agms.zone_management_service.dto.ZoneResponseDTO;
import com.agms.zone_management_service.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    @Autowired
    private ZoneService service;

    @PostMapping
    public ZoneResponseDTO create(@RequestBody ZoneDTO dto) {
        // FIX: Return ZoneResponseDTO (not Zone entity) so deviceId is always serialized
        return service.createZone(dto);
    }

    @GetMapping("/{id}")
    public ZoneResponseDTO get(@PathVariable Long id) {
        return service.getZone(id);
    }

    @GetMapping
    public List<ZoneResponseDTO> getAll() {
        return service.getAllZones();
    }

    @PutMapping("/{id}")
    public ZoneResponseDTO update(@PathVariable Long id, @RequestBody ZoneDTO dto) {
        return service.updateZone(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteZone(id);
    }
}