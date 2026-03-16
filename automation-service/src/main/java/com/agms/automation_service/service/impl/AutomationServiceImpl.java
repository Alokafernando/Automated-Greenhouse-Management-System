package com.agms.automation_service.service.impl;

import com.agms.automation_service.dto.ActionResponseDTO;
import com.agms.automation_service.dto.SensorDataDTO;
import com.agms.automation_service.service.AutomationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AutomationServiceImpl implements AutomationService {

    @Value("${automation.thresholds.temperature:30.0}")
    private double tempThreshold;

    @Value("${automation.thresholds.humidity:60.0}")
    private double humidityThreshold;

    @Override
    public ActionResponseDTO evaluateTelemetry(SensorDataDTO data) {
        log.info("Evaluating data for Sensor: {} | Type: {} | Value: {}",
                data.getSensorId(), data.getSensorType(), data.getValue());

        ActionResponseDTO response = new ActionResponseDTO();
        response.setSensorId(data.getSensorId());
        response.setProcessedAt(LocalDateTime.now());

        if ("TEMPERATURE".equalsIgnoreCase(data.getSensorType())) {
            if (data.getValue() > tempThreshold) {
                response.setAction("ACTIVATE_COOLING");
                response.setStatus("CRITICAL");
                response.setMessage("Temperature " + data.getValue() + "C exceeds threshold of " + tempThreshold + "C");
            } else {
                response.setAction("NO_ACTION");
                response.setStatus("NORMAL");
                response.setMessage("Temperature is within safe limits.");
            }
        }
        else if ("HUMIDITY".equalsIgnoreCase(data.getSensorType())) {
            if (data.getValue() > humidityThreshold) {
                response.setAction("ACTIVATE_DEHUMIDIFIER");
                response.setStatus("WARNING");
                response.setMessage("Humidity level " + data.getValue() + "% is too high.");
            } else {
                response.setAction("NO_ACTION");
                response.setStatus("NORMAL");
                response.setMessage("Humidity level stable.");
            }
        }
        else {
            response.setAction("MONITORING");
            response.setStatus("UNKNOWN_TYPE");
            response.setMessage("Sensor type not recognized for automation rules.");
        }

        log.info("Decision: {} for Sensor: {}", response.getAction(), data.getSensorId());
        return response;
    }
}