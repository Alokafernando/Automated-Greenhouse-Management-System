package com.agms.zone_management_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "sensor-service")
public interface SensorClient {

    @PostMapping("/api/sensors/devices")
    Map<String, Object> registerDevice(@RequestBody Map<String, Object> deviceRequest);

}