package com.SmartPackageSurveillance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SmartPackageSurveillance.dto.AlertResponse;
import com.SmartPackageSurveillance.entity.Alert;
import com.SmartPackageSurveillance.entity.Container;
import com.SmartPackageSurveillance.entity.Package;
import com.SmartPackageSurveillance.enumaration.AlertType;
import com.SmartPackageSurveillance.exception.AlertNotFoundException;
import com.SmartPackageSurveillance.repository.AlertRepository;
import com.SmartPackageSurveillance.repository.PackageRepository;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final PackageRepository packageRepository;

    @Value("${alert.temperature.max:30}")
    private double maxTemperature;

    @Value("${alert.humidity.max:80}")
    private double maxHumidity;

    public AlertService(AlertRepository alertRepository, PackageRepository packageRepository) {
        this.alertRepository = alertRepository;
        this.packageRepository = packageRepository;
    }

    /** Applies the alert rules to a reading and stores one alert per violated rule. */
    @Transactional
    public void checkSensorRules(double temperature, double humidity, boolean opened,
                                 boolean shockDetected, Container container) {
        if (temperature > maxTemperature) {
            createAlert(AlertType.HIGH_TEMPERATURE,
                    "Temperature is too high: " + temperature + " C", container);
        }
        if (humidity > maxHumidity) {
            createAlert(AlertType.HIGH_HUMIDITY,
                    "Humidity is too high: " + humidity + " %", container);
        }
        if (opened) {
            createAlert(AlertType.DOOR_OPENED, "Container door was opened", container);
        }
        if (shockDetected) {
            createAlert(AlertType.SHOCK_DETECTED, "Abnormal movement detected", container);
        }
    }

    private void createAlert(AlertType type, String msg, Container container) {
        Alert alert = new Alert(type, msg, container);
        List<Package> packages = packageRepository.findByContainerId(container.getId());
        if (!packages.isEmpty()) {
            alert.setPackageEntity(packages.get(0));
        }
        alertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getAlerts(boolean onlyUnresolved) {
        List<Alert> alerts = onlyUnresolved
                ? alertRepository.findByResolvedFalseOrderByCreatedAtDesc()
                : alertRepository.findAllByOrderByCreatedAtDesc();
        return alerts.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsByPackage(Long packageId) {
        return alertRepository.findByPackageEntityIdOrderByCreatedAtDesc(packageId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AlertResponse resolve(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found"));
        alert.setResolved(true);
        return toResponse(alertRepository.save(alert));
    }

    private AlertResponse toResponse(Alert a) {
        return new AlertResponse(a.getId(), a.getType(), a.getMessage(), a.isResolved(),
                a.getCreatedAt(),
                a.getContainer() != null ? a.getContainer().getContainerCode() : null,
                a.getPackageEntity() != null ? a.getPackageEntity().getPackageCode() : null);
    }
}
