package com.SmartPackageSurveillance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SmartPackageSurveillance.dto.*;
import com.SmartPackageSurveillance.service.*;

import jakarta.validation.Valid;

/** Everything under /api/admin requires the ADMIN role (see SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ContainerService containerService;
    private final PackageService packageService;
    private final AlertService alertService;
    private final SensorDataService sensorDataService;
    private final UserService userService;

    public AdminController(ContainerService containerService, PackageService packageService,
                           AlertService alertService, SensorDataService sensorDataService,
                           UserService userService) {
        this.containerService = containerService;
        this.packageService = packageService;
        this.alertService = alertService;
        this.sensorDataService = sensorDataService;
        this.userService = userService;
    }

    // ---- users ----
    @GetMapping("/users")
    public List<UserResponse> users() {
        return userService.getAllUsers();
    }

    /** Body: {"email":"someone@mail.com","role":"ADMIN"} (or "CUSTOMER") */
    @PatchMapping("/users/role")
    public UserResponse changeRole(@RequestBody java.util.Map<String, String> body) {
        return userService.changeRole(body.get("email"), body.get("role"));
    }

    // ---- containers ----
    @PostMapping("/containers")
    public ResponseEntity<ContainerResponse> createContainer(@Valid @RequestBody ContainerRequest dto) {
        return ResponseEntity.status(201).body(containerService.createContainer(dto));
    }

    @GetMapping("/containers")
    public List<ContainerResponse> containers() {
        return containerService.getAll();
    }

    @GetMapping("/containers/{code}")
    public ContainerResponse container(@PathVariable String code) {
        return containerService.getByCode(code);
    }

    @PatchMapping("/containers/{code}/status")
    public ContainerResponse containerStatus(@PathVariable String code, @Valid @RequestBody StatusRequest dto) {
        return containerService.updateStatus(code, dto.status());
    }

    @GetMapping("/containers/{code}/sensor-data")
    public List<SensorDataResponse> sensorHistory(@PathVariable String code,
                                                  @RequestParam(defaultValue = "60") int limit) {
        return sensorDataService.history(code, limit);
    }

    // ---- packages ----
    @PostMapping("/packages")
    public ResponseEntity<PackageResponse> createPackage(@Valid @RequestBody PackageRequest dto) {
        return ResponseEntity.status(201).body(packageService.create(dto));
    }

    @GetMapping("/packages")
    public List<PackageResponse> packages() {
        return packageService.getAll();
    }

    @PatchMapping("/packages/{code}/status")
    public PackageResponse packageStatus(@PathVariable String code, @Valid @RequestBody StatusRequest dto) {
        return packageService.updateStatus(code, dto.status());
    }

    // ---- alerts ----
    @GetMapping("/alerts")
    public List<AlertResponse> alerts(@RequestParam(defaultValue = "false") boolean unresolved) {
        return alertService.getAlerts(unresolved);
    }

    @PatchMapping("/alerts/{id}/resolve")
    public AlertResponse resolve(@PathVariable Long id) {
        return alertService.resolve(id);
    }
}
