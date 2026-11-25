package com.SmartPackageSurveillance.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.SmartPackageSurveillance.enumaration.PackageStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "package")
public class Package {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "package_id")
	private Long id;

	@Column(name = "package_code", nullable = false, unique = true)
	private String packageCode;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PackageStatus status = PackageStatus.CREATED;

	@Column(name = "current_location")
	private String currentLocation;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "opened", nullable = false)
	private boolean opened;

	@Column(name = "estimated_delivery")
	private LocalDateTime estimatedDelivery;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "container_id")
	private Container container;

	@OneToMany(mappedBy = "packageEntity")
	private List<Alert> alerts = new ArrayList<>();

	public Package() {
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getPackageCode() { return packageCode; }
	public void setPackageCode(String packageCode) { this.packageCode = packageCode; }
	public PackageStatus getStatus() { return status; }
	public void setStatus(PackageStatus status) { this.status = status; }
	public String getCurrentLocation() { return currentLocation; }
	public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
	public Double getLatitude() { return latitude; }
	public void setLatitude(Double latitude) { this.latitude = latitude; }
	public Double getLongitude() { return longitude; }
	public void setLongitude(Double longitude) { this.longitude = longitude; }
	public boolean isOpened() { return opened; }
	public void setOpened(boolean opened) { this.opened = opened; }
	public LocalDateTime getEstimatedDelivery() { return estimatedDelivery; }
	public void setEstimatedDelivery(LocalDateTime estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }
	public Container getContainer() { return container; }
	public void setContainer(Container container) { this.container = container; }
	public List<Alert> getAlerts() { return alerts; }
	public void setAlerts(List<Alert> alerts) { this.alerts = alerts; }
}
