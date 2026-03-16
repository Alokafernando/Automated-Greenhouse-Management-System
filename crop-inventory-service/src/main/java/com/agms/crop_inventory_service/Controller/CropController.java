package com.agms.crop_inventory_service.Controller;

import com.agms.crop_inventory_service.entity.Crop;

import com.agms.crop_inventory_service.entity.CropStatus;
import com.agms.crop_inventory_service.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
public class CropController {

    private final CropService cropService;

    @PostMapping
    public ResponseEntity<Crop> registerBatch(@RequestBody Crop crop) {
        return ResponseEntity.ok(cropService.registerBatch(crop));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Crop> updateStatus(
            @PathVariable Long id,
            @RequestParam CropStatus status) {
        return ResponseEntity.ok(cropService.updateCropStatus(id, status));
    }

    @GetMapping
    public ResponseEntity<List<Crop>> getAllCrops() {
        return ResponseEntity.ok(cropService.getAllCrops());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Crop> getCrop(@PathVariable Long id) {
        return ResponseEntity.ok(cropService.getCropById(id));
    }
}