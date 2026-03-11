package com.agms.sensor_telemetry_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class SensorTelemetryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorTelemetryServiceApplication.class, args);
	}

}
