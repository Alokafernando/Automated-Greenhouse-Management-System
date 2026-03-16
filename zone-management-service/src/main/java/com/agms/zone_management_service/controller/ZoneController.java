package com.agms.zone_management_service.controller;

import com.agms.zone_management_service.dto.ApiResponse;
import com.agms.zone_management_service.dto.ZoneDTO;
import com.agms.zone_management_service.entity.Zone;
import com.agms.zone_management_service.service.ZoneService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse> createZone(@RequestBody ZoneDTO zoneDTO) {
        Zone createdZone = zoneService.createZone(zoneDTO);
        return ResponseEntity.ok(new ApiResponse(200, "Zone created successfully", createdZone));
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