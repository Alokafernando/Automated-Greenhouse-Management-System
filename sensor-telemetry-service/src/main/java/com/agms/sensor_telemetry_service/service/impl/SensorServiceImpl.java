package com.agms.sensor_telemetry_service.service.impl;

import com.agms.sensor_telemetry_service.client.AutomationClient;
import com.agms.sensor_telemetry_service.client.ExternalIoTClient;
import com.agms.sensor_telemetry_service.dto.DeviceDTO;
import com.agms.sensor_telemetry_service.dto.ZoneDTO;
import com.agms.sensor_telemetry_service.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {

    private final AutomationClient automationClient;
    private final ExternalIoTClient zoneClient;
    private final ModelMapper modelMapper;

    @Override
    public void processTelemetry(com.agms.sensor_service.dto.SensorTelemetryDTO telemetryDTO) {
        // Convert SensorTelemetryDTO to DeviceDTO
        DeviceDTO deviceDTO = modelMapper.map(telemetryDTO, DeviceDTO.class);

        // Send data to Automation Service
        automationClient.processTelemetry(deviceDTO);
    }

    @Override
    public List<ZoneDTO> getAllZones() {
        return zoneClient.getAllZones();
    }
}