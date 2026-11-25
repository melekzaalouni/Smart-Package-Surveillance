package com.SmartPackageSurveillance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SmartPackageSurveillance.dto.SensorDataRequest;
import com.SmartPackageSurveillance.dto.SensorDataResponse;
import com.SmartPackageSurveillance.entity.Container;
import com.SmartPackageSurveillance.entity.Package;
import com.SmartPackageSurveillance.entity.SensorData;
import com.SmartPackageSurveillance.enumaration.ContainerStatus;
import com.SmartPackageSurveillance.enumaration.PackageStatus;
import com.SmartPackageSurveillance.exception.InvalidSensorDataException;
import com.SmartPackageSurveillance.repository.ContainerRepository;
import com.SmartPackageSurveillance.repository.PackageRepository;
import com.SmartPackageSurveillance.repository.SensorDataRepository;

@Service
public class SensorDataService {

    private final SensorDataRepository sensorDataRepository;
    private final ContainerRepository containerRepository;
    private final PackageRepository packageRepository;
    private final ContainerService containerService;
    private final AlertService alertService;

    public SensorDataService(SensorDataRepository sensorDataRepository,
                             ContainerRepository containerRepository,
                             PackageRepository packageRepository,
                             ContainerService containerService,
                             AlertService alertService) {
        this.sensorDataRepository = sensorDataRepository;
        this.containerRepository = containerRepository;
        this.packageRepository = packageRepository;
        this.containerService = containerService;
        this.alertService = alertService;
    }

    /** Called by the ESP32: stores the reading, updates position/status, triggers alerts. */
    @Transactional
    public SensorDataResponse ingest(SensorDataRequest dto) {
        if (dto.getHumidity() < 0 || dto.getHumidity() > 100) {
            throw new InvalidSensorDataException("Humidity must be between 0 and 100");
        }
        if (dto.getTemperature() < -60 || dto.getTemperature() > 125) {
            throw new InvalidSensorDataException("Temperature out of sensor range");
        }
        if (dto.getLatitude() < -90 || dto.getLatitude() > 90
                || dto.getLongitude() < -180 || dto.getLongitude() > 180) {
            throw new InvalidSensorDataException("Invalid GPS coordinates");
        }

        Container container = containerService.getEntityByCode(dto.getContainerCode());

        SensorData data = new SensorData(dto.getTemperature(), dto.getHumidity(), dto.getLatitude(),
                dto.getLongitude(), dto.isOpened(), dto.isShockDetected(), LocalDateTime.now(), container);
        SensorData saved = sensorDataRepository.save(data);

        container.setLatitude(dto.getLatitude());
        container.setLongitude(dto.getLongitude());
        if (container.getStatus() == ContainerStatus.AVAILABLE) {
            container.setStatus(ContainerStatus.IN_TRANSIT);
        }
        containerRepository.save(container);

        List<Package> packages = packageRepository.findByContainerId(container.getId());
        for (Package p : packages) {
            if (p.getStatus() == PackageStatus.DELIVERED || p.getStatus() == PackageStatus.CANCELLED) {
                continue;
            }
            p.setLatitude(dto.getLatitude());
            p.setLongitude(dto.getLongitude());
            if (dto.isOpened()) {
                p.setOpened(true);
            }
            if (p.getStatus() == PackageStatus.CREATED) {
                p.setStatus(PackageStatus.IN_TRANSIT);
            }
            packageRepository.save(p);
        }

        alertService.checkSensorRules(dto.getTemperature(), dto.getHumidity(),
                dto.isOpened(), dto.isShockDetected(), container);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SensorDataResponse> history(String containerCode) {
        return history(containerCode, 500);
    }

    /** Newest readings first, capped (1..500) so dashboards stay fast. */
    @Transactional(readOnly = true)
    public List<SensorDataResponse> history(String containerCode, int limit) {
        Container container = containerService.getEntityByCode(containerCode);
        return sensorDataRepository.findByContainerIdOrderByRecordedAtDesc(container.getId())
                .stream().limit(Math.max(1, Math.min(limit, 500))).map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SensorData latest(Long containerId) {
        return sensorDataRepository.findFirstByContainerIdOrderByRecordedAtDesc(containerId).orElse(null);
    }

    private SensorDataResponse toResponse(SensorData s) {
        return new SensorDataResponse(s.getId(), s.getTemperature(), s.getHumidity(), s.getLatitude(),
                s.getLongitude(), s.isOpened(), s.isShockDetected(), s.getRecordedAt());
    }
}
