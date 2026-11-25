package com.SmartPackageSurveillance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SmartPackageSurveillance.entity.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByContainerIdOrderByCreatedAtDesc(Long containerId);

    List<Alert> findByResolvedFalseOrderByCreatedAtDesc();

    List<Alert> findAllByOrderByCreatedAtDesc();

    List<Alert> findByPackageEntityIdOrderByCreatedAtDesc(Long packageId);
}
