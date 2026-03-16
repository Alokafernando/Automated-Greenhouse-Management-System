package com.agms.crop_inventory_service.repository;

import com.agms.crop_inventory_service.entity.Crop;
import com.agms.crop_inventory_service.entity.CropStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByStatus(CropStatus status);

    List<Crop> findByZoneId(String zoneId);
}