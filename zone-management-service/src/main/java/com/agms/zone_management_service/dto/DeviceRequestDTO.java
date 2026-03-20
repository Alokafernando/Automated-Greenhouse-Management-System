package com.agms.zone_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceRequestDTO {
    private String name;
    private String zoneId;
}