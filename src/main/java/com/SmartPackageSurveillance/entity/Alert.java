package com.SmartPackageSurveillance.entity;

import java.time.LocalDateTime;

import com.SmartPackageSurveillance.enumaration.AlertType;

import jakarta.persistence.*;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean resolved;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "container_id")
    private Container container;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private Package packageEntity;


    // No-argument constructor
    public Alert() {
    }
    
    public Alert(AlertType type, String message, Container container) {
		this.type = type;
		this.message = message;
		this.container = container;
		this.createdAt = LocalDateTime.now();
	}

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

	// Constructor
    public Alert(AlertType type, String message, boolean resolved,
                 LocalDateTime createdAt, Container container,
                 Package packageEntity) {

        this.type = type;
        this.message = message;
        this.resolved = resolved;
        this.createdAt = createdAt;
        this.container = container;
        this.packageEntity = packageEntity;
    }


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public Package getPackageEntity() {
        return packageEntity;
    }

    public void setPackageEntity(Package packageEntity) {
        this.packageEntity = packageEntity;
    }
}
