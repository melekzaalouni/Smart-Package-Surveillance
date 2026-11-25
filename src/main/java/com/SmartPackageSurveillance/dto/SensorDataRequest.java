package com.SmartPackageSurveillance.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SensorDataRequest {

	public SensorDataRequest() {
	}

	@NotBlank
    private String containerCode ;
	@NotNull
    private Double temperature;
	@NotNull
    private Double humidity;
	@NotNull
    private Double latitude;
	@NotNull
    private Double longitude;
	    private boolean opened;
	    private boolean shockDetected;
	public SensorDataRequest(String containerCode, Double temperature,
			Double humidity, Double latitude, Double longitude, boolean opened,
			boolean shockDetected) {
		super();
		this.containerCode = containerCode;
		this.temperature = temperature;
		this.humidity = humidity;
		this.latitude = latitude;
		this.longitude = longitude;
		this.opened = opened;
		this.shockDetected = shockDetected;
	}
	public String getContainerCode() {
		return containerCode;
	}
	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}
	public Double getTemperature() {
		return temperature;
	}
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
	public Double getHumidity() {
		return humidity;
	}
	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public boolean isOpened() {
		return opened;
	}
	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	public boolean isShockDetected() {
		return shockDetected;
	}
	public void setShockDetected(boolean shockDetected) {
		this.shockDetected = shockDetected;
	}
	

}
