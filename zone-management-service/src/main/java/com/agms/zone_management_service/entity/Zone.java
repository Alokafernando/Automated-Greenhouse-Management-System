package com.agms.zone_management_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double minTemp;
    private double maxTemp;

    private double minHumidity;
    private double maxHumidity;

    private String deviceId;
}