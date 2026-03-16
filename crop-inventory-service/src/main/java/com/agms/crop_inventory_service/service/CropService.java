package com.agms.crop_inventory_service.service;

import com.agms.crop_inventory_service.entity.Crop;
import com.agms.crop_inventory_service.entity.CropStatus;
import java.util.List;

public interface CropService {
    Crop registerBatch(Crop crop);
    Crop updateCropStatus(Long id, CropStatus newStatus);
    List<Crop> getAllCrops();
    Crop getCropById(Long id);
}