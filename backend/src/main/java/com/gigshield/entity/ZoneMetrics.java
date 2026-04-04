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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
