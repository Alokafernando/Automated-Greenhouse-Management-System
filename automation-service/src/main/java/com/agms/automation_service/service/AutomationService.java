package com.agms.automation_service.service;

import com.agms.automation_service.dto.ActionResponseDTO;
import com.agms.automation_service.dto.SensorDataDTO;

public interface AutomationService {
    ActionResponseDTO evaluateTelemetry(SensorDataDTO data);
}
