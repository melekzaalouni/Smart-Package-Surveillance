package com.SmartPackageSurveillance.service;

import com.SmartPackageSurveillance.dto.SensorDataResponse;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SmartPackageSurveillance.dto.AlertResponse;
import com.SmartPackageSurveillance.dto.CustomerResponse;
import com.SmartPackageSurveillance.dto.PackageRequest;
import com.SmartPackageSurveillance.dto.PackageResponse;
import com.SmartPackageSurveillance.entity.Container;
import com.SmartPackageSurveillance.entity.Package;
import com.SmartPackageSurveillance.entity.SensorData;
import com.SmartPackageSurveillance.entity.User;
import com.SmartPackageSurveillance.enumaration.PackageStatus;
import com.SmartPackageSurveillance.enumaration.UserRole;
import com.SmartPackageSurveillance.exception.PackageNotFoundException;
import com.SmartPackageSurveillance.exception.UserNotFoundException;
import com.SmartPackageSurveillance.repository.PackageRepository;
import com.SmartPackageSurveillance.repository.UserRepository;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final ContainerService containerService;
    private final SensorDataService sensorDataService;
    private final AlertService alertService;

    public PackageService(PackageRepository packageRepository, UserRepository userRepository,
                          ContainerService containerService, SensorDataService sensorDataService,
                          AlertService alertService) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
        this.containerService = containerService;
        this.sensorDataService = sensorDataService;
        this.alertService = alertService;
    }

    @Transactional
    public PackageResponse create(PackageRequest dto) {
        User customer = userRepository.findByEmail(dto.customerEmail())
                .orElseThrow(() -> new UserNotFoundException("Customer not found"));
        Container container = containerService.getEntityByCode(dto.containerCode());

        Package p = new Package();
        String code = (dto.packageCode() == null || dto.packageCode().isBlank())
                ? "PKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                : dto.packageCode();
        if (packageRepository.findByPackageCode(code).isPresent()) {
            throw new IllegalArgumentException("Package code already exists");
        }
        p.setPackageCode(code);
        p.setStatus(PackageStatus.CREATED);
        p.setCurrentLocation(dto.currentLocation());
        p.setEstimatedDelivery(dto.estimatedDelivery());
        p.setLatitude(container.getLatitude());
        p.setLongitude(container.getLongitude());
        p.setUser(customer);
        p.setContainer(container);
        return toResponse(packageRepository.save(p));
    }

    @Transactional(readOnly = true)
    public List<PackageResponse> getAll() {
        return packageRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PackageResponse> getMyPackages(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return packageRepository.findByUserId(user.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PackageResponse getByCode(String code, String email) {
        return toResponse(getAuthorized(code, email));
    }

    @Transactional
    public PackageResponse updateStatus(String code, String status) {
        Package p = packageRepository.findByPackageCode(code)
                .orElseThrow(() -> new PackageNotFoundException("Package not found: " + code));
        p.setStatus(PackageStatus.valueOf(status.toUpperCase()));
        return toResponse(packageRepository.save(p));
    }

    /** Latest sensor reading of the container carrying this package (customer view). */
    @Transactional(readOnly = true)
    public CustomerResponse getTracking(String code, String email) {
        Package p = getAuthorized(code, email);
        Container c = p.getContainer();
        if (c == null) {
            throw new PackageNotFoundException("Package is not assigned to a container");
        }
        SensorData s = sensorDataService.latest(c.getId());
        if (s == null) {
            return new CustomerResponse(c.getContainerCode(), null, null,
                    c.getLatitude(), c.getLongitude(), p.isOpened(), false);
        }
        return new CustomerResponse(c.getContainerCode(), s.getTemperature(), s.getHumidity(),
                s.getLatitude(), s.getLongitude(), s.isOpened(), s.isShockDetected());
    }

    /** Recent readings of the container carrying this package (owner or admin). */
    @Transactional(readOnly = true)
    public List<SensorDataResponse> getHistory(String code, String email, int limit) {
        Package p = getAuthorized(code, email);
        if (p.getContainer() == null) {
            return List.of();
        }
        return sensorDataService.history(p.getContainer().getContainerCode(), limit);
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getAlerts(String code, String email) {
        return alertService.getAlertsByPackage(getAuthorized(code, email).getId());
    }

    /** Admins can see any package; customers only their own. */
    private Package getAuthorized(String code, String email) {
        Package p = packageRepository.findByPackageCode(code)
                .orElseThrow(() -> new PackageNotFoundException("Package not found: " + code));
        User caller = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        boolean owner = p.getUser() != null && p.getUser().getId().equals(caller.getId());
        if (caller.getRole() != UserRole.ADMIN && !owner) {
            throw new AccessDeniedException("Not your package");
        }
        return p;
    }

    private PackageResponse toResponse(Package p) {
        return new PackageResponse(p.getId(), p.getPackageCode(), p.getStatus(), p.getCurrentLocation(),
                p.getLatitude(), p.getLongitude(), p.isOpened(), p.getEstimatedDelivery(),
                p.getUser() != null ? p.getUser().getEmail() : null,
                p.getContainer() != null ? p.getContainer().getContainerCode() : null);
    }
}
