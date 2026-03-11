package com.agms.sensor_telemetry_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ZoneDTO {

    private Long id;
    private String name;
    private double minTemp;
    private double maxTemp;
    private String deviceId;
    private String userId;

}