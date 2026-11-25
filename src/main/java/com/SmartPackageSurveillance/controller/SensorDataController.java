package com.SmartPackageSurveillance.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SmartPackageSurveillance.dto.SensorDataRequest;
import com.SmartPackageSurveillance.dto.SensorDataResponse;
import com.SmartPackageSurveillance.service.SensorDataService;

import jakarta.validation.Valid;

/** Endpoint called by the ESP32. Authenticated with the X-Device-Key header. */
@RestController
@RequestMapping("/api/sensor-data")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    @Value("${device.api-key}")
    private String deviceApiKey;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @PostMapping
    public ResponseEntity<?> receive(@RequestHeader(value = "X-Device-Key", required = false) String key,
                                     @Valid @RequestBody SensorDataRequest dto) {
        if (key == null || !key.equals(deviceApiKey)) {
            return ResponseEntity.status(401).body(java.util.Map.of("message", "Invalid device key"));
        }
        SensorDataResponse saved = sensorDataService.ingest(dto);
        return ResponseEntity.status(201).body(saved);
    }
}
