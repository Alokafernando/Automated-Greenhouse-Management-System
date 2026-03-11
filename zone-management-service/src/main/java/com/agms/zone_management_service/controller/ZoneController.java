package com.agms.zone_management_service.controller;

import com.agms.zone_management_service.dto.ZoneDTO;
import com.agms.zone_management_service.entity.Zone;
import com.agms.zone_management_service.service.ZoneService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/zones")
public class ZoneController {

    private final ZoneService service;

    public ZoneController(ZoneService service) {
        this.service = service;
    }

    @PostMapping
    public Zone createZone(@RequestBody ZoneDTO dto){
        return service.createZone(dto);
    }

    @GetMapping("/{id}")
    public Zone getZone(@PathVariable Long id){
        return service.getZoneById(id);
    }

    @PutMapping("/{id}")
    public Zone updateZone(@PathVariable Long id,
                           @RequestBody ZoneDTO dto){
        return service.updateZone(id,dto);
    }

    @DeleteMapping("/{id}")
    public void deleteZone(@PathVariable Long id){
        service.deleteZone(id);
    }
}