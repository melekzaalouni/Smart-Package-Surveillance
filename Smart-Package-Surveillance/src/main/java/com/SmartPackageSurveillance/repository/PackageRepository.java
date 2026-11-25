package com.SmartPackageSurveillance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SmartPackageSurveillance.entity.Package;

public interface PackageRepository extends JpaRepository<Package, Long> {
    Optional<Package> findByPackageCode(String packageCode);

    List<Package> findByUserId(Long userId);

    List<Package> findByContainerId(Long containerId);
}
