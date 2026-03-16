package com.agms.sensor_telemetry_service.client;

import com.agms.sensor_telemetry_service.dto.ZoneDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "zone-management-service")
public interface ZoneClient {

    @GetMapping("/api/zones")
    List<ZoneDTO> getAllZones();
}