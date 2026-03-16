package com.agms.automation_service.controller;

import com.agms.automation_service.dto.ActionResponseDTO;
import com.agms.automation_service.dto.SensorDataDTO;
import com.agms.automation_service.service.AutomationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/automation")
public class AutomationController {

    private final AutomationService automationService;

    public AutomationController(AutomationService automationService) {
        this.automationService = automationService;
    }

    @PostMapping("/process")
    public ResponseEntity<ActionResponseDTO> processData(@RequestBody SensorDataDTO data) {
        ActionResponseDTO result = automationService.evaluateTelemetry(data);
        return ResponseEntity.ok(result);
    }
}