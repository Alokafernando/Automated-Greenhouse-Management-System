package com.agms.automation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionResponseDTO {
    private String sensorId;
    private String action;
    private String status;
    private String message;
    private LocalDateTime processedAt = LocalDateTime.now();
}
