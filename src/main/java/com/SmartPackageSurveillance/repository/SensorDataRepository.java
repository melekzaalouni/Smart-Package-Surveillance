package com.SmartPackageSurveillance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SmartPackageSurveillance.entity.SensorData;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findByContainerIdOrderByRecordedAtDesc(Long containerId);

    Optional<SensorData> findFirstByContainerIdOrderByRecordedAtDesc(Long containerId);
}
