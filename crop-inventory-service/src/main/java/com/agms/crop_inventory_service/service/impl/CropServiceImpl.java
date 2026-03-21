package com.agms.crop_inventory_service.service.impl;

import com.agms.crop_inventory_service.entity.Crop;
import com.agms.crop_inventory_service.entity.CropStatus;
import com.agms.crop_inventory_service.repository.CropRepository;
import com.agms.crop_inventory_service.service.CropService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;

    @Override
    public Crop registerBatch(Crop crop) {
        crop.setStatus(CropStatus.SEEDLING);
        crop.setPlantedDate(LocalDate.now());
        log.info("Registering new crop batch: {}", crop.getBatchName());
        return cropRepository.save(crop);
    }

    @Override
    public Crop updateCropStatus(Long id, CropStatus newStatus) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop batch not found with id: " + id));

        if (isValidTransition(crop.getStatus(), newStatus)) {
            log.info("Updating crop {} status: {} -> {}", id, crop.getStatus(), newStatus);
            crop.setStatus(newStatus);

            if (newStatus == CropStatus.HARVESTED) {
                crop.setHarvestDate(LocalDate.now());
            }

            return cropRepository.save(crop);
        } else {
            log.error("Invalid lifecycle transition: {} to {}", crop.getStatus(), newStatus);
            throw new IllegalStateException("Cannot move lifecycle from " + crop.getStatus() + " to " + newStatus);
        }
    }

    @Override
    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    @Override
    public Crop getCropById(Long id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found with id: " + id));
    }

    private boolean isValidTransition(CropStatus current, CropStatus next) {
        return switch (current) {
            case SEEDLING -> next == CropStatus.VEGETATIVE;
            case VEGETATIVE -> next == CropStatus.HARVESTED;
            case HARVESTED -> false;
        };
    }
}