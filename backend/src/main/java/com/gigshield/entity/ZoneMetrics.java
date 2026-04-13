package com.gigshield.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
@Table(name = "zone_metrics",
       uniqueConstraints = @UniqueConstraint(columnNames = {"city", "zone", "date"}))
public class ZoneMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String zone;

    /** The calendar date this metric row represents. */
    @Column(nullable = false)
    private LocalDate date;


    private Double maxRainfallMm;


    private Double avgRainfallMm;

    private Double maxTemperatureCelsius;

    
    private Boolean cycloneAlertIssued = false;


    private String imdAlertLevel;


    private Double avgAqi;

  
    private Double maxAqi;

    private Boolean curfewDeclared = false;

   
    private Boolean platformOutageDetected = false;

    private Boolean disruptionEventOccurred = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void setCity(String city) { this.city = city; }
    public void setZone(String zone) { this.zone = zone; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setMaxRainfallMm(Double v) { this.maxRainfallMm = v; }
    public void setAvgRainfallMm(Double v) { this.avgRainfallMm = v; }
    public void setMaxTemperatureCelsius(Double v) { this.maxTemperatureCelsius = v; }
    public void setCycloneAlertIssued(Boolean b) { this.cycloneAlertIssued = b; }
    public void setImdAlertLevel(String s) { this.imdAlertLevel = s; }
    public void setAvgAqi(Double v) { this.avgAqi = v; }
    public void setMaxAqi(Double v) { this.maxAqi = v; }
    public void setCurfewDeclared(Boolean b) { this.curfewDeclared = b; }
    public void setPlatformOutageDetected(Boolean b) { this.platformOutageDetected = b; }
    public void setDisruptionEventOccurred(Boolean b) { this.disruptionEventOccurred = b; }

    public double getAvgAqi() { return avgAqi != null ? avgAqi : 0.0; }
    public double getAvgRainfallMm() { return avgRainfallMm != null ? avgRainfallMm : 0.0; }
    public double getMaxTemperatureCelsius() { return maxTemperatureCelsius != null ? maxTemperatureCelsius : 0.0; }
    public boolean getDisruptionEventOccurred() { return disruptionEventOccurred != null ? disruptionEventOccurred : false; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
