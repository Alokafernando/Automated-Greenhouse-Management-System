package com.agms.sensor_telemetry_service.scheduledfetcher;

import com.agms.sensor_telemetry_service.Model.Telemetry;
import com.agms.sensor_telemetry_service.client.AutomationClient;
import com.agms.sensor_telemetry_service.client.ExternalIoTClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SensorFetcher {

    private final ExternalIoTClient externalIoTClient;
    private final AutomationClient automationClient;

    private volatile Telemetry latestTelemetry;

    public SensorFetcher(ExternalIoTClient externalIoTClient, AutomationClient automationClient) {
        this.externalIoTClient = externalIoTClient;
        this.automationClient = automationClient;
    }

    // Runs every 10 seconds
    @Scheduled(fixedRate = 10000)
    public void fetchAndPushTelemetry() {
        String deviceId = "b751b8c9-644a-484c-ba3f-be63f9b27ad0";
        String token = "your-access-token"; // Replace with actual JWT/token logic

        externalIoTClient.fetchTelemetry(deviceId, token)  // <-- call the method properly
                .doOnNext(telemetry -> {
                    this.latestTelemetry = telemetry;        // store latest for debug
                    automationClient.sendDataToBrain(telemetry); // push to automation service
                })
                .subscribe(); // reactive subscription
    }

    public Telemetry getLatestTelemetry() {
        return latestTelemetry;
    }
}