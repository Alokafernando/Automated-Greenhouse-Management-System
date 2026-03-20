package com.agms.zone_management_service.client;

import com.agms.sensor_telemetry_service.dto.DeviceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "sensor-service")
public interface SensorClient {

    @PostMapping("/api/sensors/register")
    DeviceDTO registerDevice(@RequestBody DeviceDTO deviceRequest);
}