package com.agms.crop_inventory_service.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String batchName;
    private String variety;

    @Enumerated(EnumType.STRING)
    private CropStatus status;

    private LocalDate plantedDate;
    private LocalDate harvestDate;
    private String zoneId;
}