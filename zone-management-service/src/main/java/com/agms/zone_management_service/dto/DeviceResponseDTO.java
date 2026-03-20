package com.agms.zone_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResponseDTO {
    private String deviceId;
    private String name;
    private String zoneId;
}