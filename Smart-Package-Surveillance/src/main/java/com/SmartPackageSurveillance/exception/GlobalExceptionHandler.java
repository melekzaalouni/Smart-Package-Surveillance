package com.SmartPackageSurveillance.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<Map<String, String>> body(int status, String msg) {
		return ResponseEntity.status(status).body(Map.of("message", msg));
	}

	@ExceptionHandler(PackageNotFoundException.class)
	public ResponseEntity<Map<String, String>> handlePackageNotFound(PackageNotFoundException ex) {
		return body(404, ex.getMessage());
	}

	@ExceptionHandler(ContainerNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleContainerNotFound(ContainerNotFoundException ex) {
		return body(404, ex.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
		return body(404, ex.getMessage());
	}

	@ExceptionHandler(AlertNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleAlertNotFound(AlertNotFoundException ex) {
		return body(404, ex.getMessage());
	}

	@ExceptionHandler(InvalidSensorDataException.class)
	public ResponseEntity<Map<String, String>> handleInvalidSensorData(InvalidSensorDataException ex) {
		return body(400, ex.getMessage());
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
		return body(409, ex.getMessage());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex) {
		return body(401, ex.getMessage());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
		return body(403, "Access denied");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
		return body(400, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
		String msg = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.getField() + ": " + e.getDefaultMessage())
				.reduce((a, b) -> a + "; " + b).orElse("Invalid request data");
		return body(400, msg);
	}
}
