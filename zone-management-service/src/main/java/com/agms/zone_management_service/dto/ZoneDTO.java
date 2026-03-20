package com.agms.zone_management_service.dto;

import lombok.Data;

@Data
public class ZoneDTO {
    private String name;
    private double minTemp;
    private double maxTemp;
    private double minHumidity;   // ✅ add
    private double maxHumidity;
    private String deviceId;
}