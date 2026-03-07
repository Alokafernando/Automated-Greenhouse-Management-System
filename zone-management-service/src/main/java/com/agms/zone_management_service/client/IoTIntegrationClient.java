package com.agms.zone_management_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "iot-client")
public interface IoTIntegrationClient {

    @PostMapping("/devices")
    Map<String, String> registerDevice();

}
