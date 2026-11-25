package com.SmartPackageSurveillance.dto;

import java.time.LocalDateTime;

import com.SmartPackageSurveillance.enumaration.PackageStatus;

public record PackageResponse(
		Long id,
		String packageCode,
		PackageStatus status,
		String currentLocation,
		Double latitude,
		Double longitude,
		boolean opened,
		LocalDateTime estimatedDelivery,
		String customerEmail,
		String containerCode) {
}
