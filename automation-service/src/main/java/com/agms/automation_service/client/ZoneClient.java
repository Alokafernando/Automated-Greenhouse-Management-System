package com.agms.automation_service.client;

import com.agms.automation_service.dto.ZoneThresholdDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ZONE-MANAGEMENT-SERVICE")
public interface ZoneClient {

    @GetMapping("/api/zones/{id}")
    ZoneThresholdDTO getZoneById(@PathVariable("id") String id);
}