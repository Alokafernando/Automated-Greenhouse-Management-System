package com.agms.zone_management_service.controller;

import com.agms.zone_management_service.dto.ZoneDTO;
import com.agms.zone_management_service.entity.Zone;
import com.agms.zone_management_service.service.ZoneService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService zoneService;

    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

    @PostMapping
    public Zone createZone(@RequestBody ZoneDTO zoneDTO) {
        return zoneService.createZone(zoneDTO);
    }

    @GetMapping("/{id}")
    public Zone getZone(@PathVariable Long id) {
        return zoneService.getZoneById(id);
    }

    @PutMapping("/{id}")
    public Zone updateZone(@PathVariable Long id, @RequestBody ZoneDTO zoneDTO) {
        return zoneService.updateZone(id, zoneDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
    }

    @GetMapping
    public List<ZoneDTO> getAllZones() {
        return zoneService.findAll();
    }
}