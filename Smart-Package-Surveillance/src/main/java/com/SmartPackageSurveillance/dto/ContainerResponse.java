
package com.SmartPackageSurveillance.dto;

import com.SmartPackageSurveillance.enumaration.ContainerStatus;

public class ContainerResponse {

    private String containerCode;
    private String name;
    private ContainerStatus status;
    private Double latitude;
    private Double longitude;
    
    public ContainerResponse() {
		super();
	}

	public ContainerResponse(
            String containerCode,
            String name,
            ContainerStatus status,
            Double latitude,
            Double longitude) {

        this.containerCode = containerCode;
        this.name = name;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public String getName() {
        return name;
    }

    public ContainerStatus getStatus() {
        return status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(ContainerStatus status) {
		this.status = status;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
    
}
