package com.SmartPackageSurveillance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double temperature;

    @Column
    private Double humidity;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(nullable = false)
    private boolean opened;

    @Column(nullable = false)
    private boolean shockDetected;

    @Column(nullable = false)
    private LocalDateTime recordedAt;


    // Many SensorData -> One Container
    @ManyToOne
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;
    // No-argument constructor
    public SensorData() {
    }
    // Constructor
    public SensorData(Double temperature,
                      Double humidity,
                      Double latitude,
                      Double longitude,
                      boolean opened,
                      boolean shockDetected,
                      LocalDateTime recordedAt,
                      Container container) {

        this.temperature = temperature;
        this.humidity = humidity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.opened = opened;
        this.shockDetected = shockDetected;
        this.recordedAt = recordedAt;
        this.container = container;
    }


    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }
}
