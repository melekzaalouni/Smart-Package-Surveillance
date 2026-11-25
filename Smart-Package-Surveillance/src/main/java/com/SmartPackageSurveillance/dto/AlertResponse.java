package com.SmartPackageSurveillance.dto;

import java.time.LocalDateTime;

import com.SmartPackageSurveillance.enumaration.AlertType;

public record AlertResponse(
		Long id,
		AlertType type,
		String message,
		boolean resolved,
		LocalDateTime createdAt,
		String containerCode,
		String packageCode) {
}
