package com.SmartPackageSurveillance.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record PackageRequest(
		@NotBlank String customerEmail,
		@NotBlank String containerCode,
		String packageCode,
		String currentLocation,
		LocalDateTime estimatedDelivery) {
}
