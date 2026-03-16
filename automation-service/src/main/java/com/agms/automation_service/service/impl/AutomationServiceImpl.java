package com.agms.automation_service.service.impl;

import com.agms.automation_service.client.ZoneClient;
import com.agms.automation_service.dto.ActionResponseDTO;
import com.agms.automation_service.dto.SensorDataDTO;
import com.agms.automation_service.dto.ZoneThresholdDTO;
import com.agms.automation_service.entity.AutomationLog;
import com.agms.automation_service.repository.AutomationLogRepository;
import com.agms.automation_service.service.AutomationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutomationServiceImpl implements AutomationService {

    private final ZoneClient zoneClient;
    private final AutomationLogRepository logRepository;

    @Override
    public ActionResponseDTO evaluateTelemetry(SensorDataDTO data) {
        log.info("Processing data for Sensor: {} in Zone: {}", data.getSensorId(), data.getZoneId());

        // Using the getZoneById method from your updated ZoneClient
        ZoneThresholdDTO thresholds = zoneClient.getZoneById(data.getZoneId());

        ActionResponseDTO response = new ActionResponseDTO();
        response.setSensorId(data.getSensorId());
        response.setProcessedAt(LocalDateTime.now());

        if ("TEMPERATURE".equalsIgnoreCase(data.getSensorType())) {
            processTemperatureRules(data.getValue(), thresholds, response);
        } else {
            response.setAction("NO_ACTION");
            response.setStatus("NORMAL");
            response.setMessage("Monitoring sensor type: " + data.getSensorType());
        }

        saveToLog(data, response);

        return response;
    }

    private void processTemperatureRules(double currentVal, ZoneThresholdDTO limits, ActionResponseDTO res) {
        if (currentVal > limits.getMaxTemp()) {
            res.setAction("TURN_FAN_ON");
            res.setStatus("CRITICAL");
            res.setMessage("Temp (" + currentVal + "°C) exceeded Max (" + limits.getMaxTemp() + "°C)");
        }
        else if (currentVal < limits.getMinTemp()) {
            res.setAction("TURN_HEATER_ON");
            res.setStatus("CRITICAL");
            res.setMessage("Temp (" + currentVal + "°C) below Min (" + limits.getMinTemp() + "°C)");
        }
        else {
            res.setAction("STABLE");
            res.setStatus("NORMAL");
            res.setMessage("Temperature within optimal range.");
        }
    }

    private void saveToLog(SensorDataDTO data, ActionResponseDTO response) {
        AutomationLog logEntry = new AutomationLog();
        logEntry.setSensorId(data.getSensorId());
        logEntry.setAction(response.getAction());
        logEntry.setStatus(response.getStatus());
        logEntry.setMessage(response.getMessage());
        logEntry.setTimestamp(LocalDateTime.now());

        logRepository.save(logEntry);
        log.info("Log saved: {} for Sensor {}", response.getAction(), data.getSensorId());
    }

    public List<AutomationLog> getAllLogs() {
        return logRepository.findAllByOrderByTimestampDesc();
    }
}