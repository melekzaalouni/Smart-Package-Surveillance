package com.SmartPackageSurveillance.controller;

import com.SmartPackageSurveillance.dto.SensorDataResponse;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.SmartPackageSurveillance.dto.AlertResponse;
import com.SmartPackageSurveillance.dto.CustomerResponse;
import com.SmartPackageSurveillance.dto.PackageResponse;
import com.SmartPackageSurveillance.service.PackageService;

/** Customer endpoints (admins may also read any package). */
@RestController
@RequestMapping("/api/packages")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("/my")
    public List<PackageResponse> myPackages(Principal principal) {
        return packageService.getMyPackages(principal.getName());
    }

    @GetMapping("/{code}")
    public PackageResponse get(@PathVariable String code, Principal principal) {
        return packageService.getByCode(code, principal.getName());
    }

    @GetMapping("/{code}/tracking")
    public CustomerResponse tracking(@PathVariable String code, Principal principal) {
        return packageService.getTracking(code, principal.getName());
    }

    @GetMapping("/{code}/history")
    public List<SensorDataResponse> history(@PathVariable String code, Principal principal,
                                            @RequestParam(defaultValue = "60") int limit) {
        return packageService.getHistory(code, principal.getName(), limit);
    }

    @GetMapping("/{code}/alerts")
    public List<AlertResponse> alerts(@PathVariable String code, Principal principal) {
        return packageService.getAlerts(code, principal.getName());
    }
}
