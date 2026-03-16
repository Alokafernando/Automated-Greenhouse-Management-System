package com.agms.automation_service.controller;

import com.agms.automation_service.dto.ActionResponseDTO;
import com.agms.automation_service.dto.SensorDataDTO;
import com.agms.automation_service.entity.AutomationLog;
import com.agms.automation_service.service.impl.AutomationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/automation")
@RequiredArgsConstructor
public class AutomationController {

    private final AutomationServiceImpl automationService;

    @PostMapping("/process")
    public ResponseEntity<ActionResponseDTO> processData(@RequestBody SensorDataDTO data) {
        ActionResponseDTO result = automationService.evaluateTelemetry(data);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/logs")
    public ResponseEntity<List<AutomationLog>> getLogs() {
        List<AutomationLog> logs = automationService.getAllLogs();
        return ResponseEntity.ok(logs);
    }
}