package com.gigshield.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(nullable = false)
    private String category; 

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.PENDING;

    // --- ML Metric Fields ---
    @Column(name = "ml_decision")
    private String mlDecision;

    @Column(name = "ml_confidence")
    private Float mlConfidence;

    @Column(name = "fraud_score")
    private Float fraudScore;

    @Column(name = "worker_activity_score")
    private Float workerActivityScore;

    @Column(name = "zone_weather_verified")
    private Boolean zoneWeatherVerified;

    @Column(name = "suggested_payout_amount")
    private Integer suggestedPayoutAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = ComplaintStatus.PENDING;
    }

    public enum ComplaintStatus {
        PENDING, REVIEWED, RESOLVED, REJECTED, ACCEPTED, VERIFIED
    }
}
