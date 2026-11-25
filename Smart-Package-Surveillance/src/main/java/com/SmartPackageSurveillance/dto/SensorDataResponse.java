package com.SmartPackageSurveillance.dto;

import java.time.LocalDateTime;

public record SensorDataResponse(
		Long id,
		Double temperature,
		Double humidity,
		Double latitude,
		Double longitude,
		boolean opened,
		boolean shockDetected,
		LocalDateTime recordedAt) {
}
