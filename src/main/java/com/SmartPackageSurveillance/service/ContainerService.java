package com.SmartPackageSurveillance.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.SmartPackageSurveillance.dto.ContainerRequest;
import com.SmartPackageSurveillance.dto.ContainerResponse;
import com.SmartPackageSurveillance.entity.Container;
import com.SmartPackageSurveillance.enumaration.ContainerStatus;
import com.SmartPackageSurveillance.exception.ContainerNotFoundException;
import com.SmartPackageSurveillance.repository.ContainerRepository;

@Service
public class ContainerService {

	private final ContainerRepository containerRepository;

	public ContainerService(ContainerRepository containerRepository) {
		this.containerRepository = containerRepository;
	}

	private ContainerResponse convertToContainerResponse(Container container) {
		ContainerResponse response = new ContainerResponse();
		response.setContainerCode(container.getContainerCode());
		response.setName(container.getName());
		response.setStatus(container.getStatus());
		response.setLatitude(container.getLatitude());
		response.setLongitude(container.getLongitude());
		return response;
	}

	public ContainerResponse createContainer(ContainerRequest dto) {
		if (containerRepository.findByContainerCode(dto.getContainerCode()).isPresent()) {
			throw new IllegalArgumentException("Container code already exists");
		}
		Container container = new Container();
		container.setContainerCode(dto.getContainerCode());
		container.setName(dto.getName());
		container.setStatus(ContainerStatus.AVAILABLE);
		container.setCreatedAt(LocalDateTime.now());
		return convertToContainerResponse(containerRepository.save(container));
	}

	public List<ContainerResponse> getAll() {
		return containerRepository.findAll().stream().map(this::convertToContainerResponse).toList();
	}

	public ContainerResponse getByCode(String code) {
		return convertToContainerResponse(getEntityByCode(code));
	}

	public Container getEntityByCode(String code) {
		return containerRepository.findByContainerCode(code)
				.orElseThrow(() -> new ContainerNotFoundException("Container not found: " + code));
	}

	public ContainerResponse updateStatus(String code, String status) {
		Container container = getEntityByCode(code);
		container.setStatus(ContainerStatus.valueOf(status.toUpperCase()));
		return convertToContainerResponse(containerRepository.save(container));
	}
}
