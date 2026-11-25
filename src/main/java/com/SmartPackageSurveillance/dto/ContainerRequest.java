
package com.SmartPackageSurveillance.dto;

import jakarta.validation.constraints.NotBlank;

public class ContainerRequest {

    @NotBlank
    private String containerCode;
    @NotBlank
    private String name;

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
