package com.agms.zone_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZoneResponseDTO {
    private Long id;
    private String name;
    private double minTemp;
    private double maxTemp;
    private double minHumidity;   // ✅ add
    private double maxHumidity;
    private String deviceId;
}