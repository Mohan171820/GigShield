package com.gigshield.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    public static ComplaintBuilder builder() { return new ComplaintBuilder(); }

    public static class ComplaintBuilder {
        private Worker worker;
        private String category;
        private String description;
        private ComplaintStatus status = ComplaintStatus.PENDING;
        private String mlDecision;
        private Float mlConfidence;
        private Float fraudScore;
        private Float workerActivityScore;
        private Boolean zoneWeatherVerified;
        private Integer suggestedPayoutAmount;
        private java.time.LocalDateTime createdAt;

        public ComplaintBuilder worker(Worker w) { this.worker = w; return this; }
        public ComplaintBuilder category(String c) { this.category = c; return this; }
        public ComplaintBuilder description(String d) { this.description = d; return this; }
        public ComplaintBuilder status(ComplaintStatus s) { this.status = s; return this; }
        public ComplaintBuilder mlDecision(String m) { this.mlDecision = m; return this; }
        public ComplaintBuilder mlConfidence(Float c) { this.mlConfidence = c; return this; }
        public ComplaintBuilder fraudScore(Float f) { this.fraudScore = f; return this; }
        public ComplaintBuilder workerActivityScore(Float w) { this.workerActivityScore = w; return this; }
        public ComplaintBuilder zoneWeatherVerified(Boolean z) { this.zoneWeatherVerified = z; return this; }
        public ComplaintBuilder suggestedPayoutAmount(Integer s) { this.suggestedPayoutAmount = s; return this; }
        public ComplaintBuilder createdAt(java.time.LocalDateTime t) { this.createdAt = t; return this; }

        public Complaint build() {
            Complaint c = new Complaint();
            c.worker = this.worker;
            c.category = this.category;
            c.description = this.description;
            c.status = this.status != null ? this.status : ComplaintStatus.PENDING;
            c.mlDecision = this.mlDecision;
            c.mlConfidence = this.mlConfidence;
            c.fraudScore = this.fraudScore;
            c.workerActivityScore = this.workerActivityScore;
            c.zoneWeatherVerified = this.zoneWeatherVerified;
            c.suggestedPayoutAmount = this.suggestedPayoutAmount;
            c.createdAt = this.createdAt != null ? this.createdAt : java.time.LocalDateTime.now();
            return c;
        }
    }


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

    public Long getId() { return id; }
    public Worker getWorker() { return worker; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public ComplaintStatus getStatus() { return status; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    
    public String getMlDecision() { return mlDecision; }
    public Float getMlConfidence() { return mlConfidence; }
    public Float getFraudScore() { return fraudScore; }
    public Float getWorkerActivityScore() { return workerActivityScore; }
    public Boolean getZoneWeatherVerified() { return zoneWeatherVerified; }
    public Integer getSuggestedPayoutAmount() { return suggestedPayoutAmount; }

    public void setStatus(ComplaintStatus status) { this.status = status; }
    public void setSuggestedPayoutAmount(Integer amount) { this.suggestedPayoutAmount = amount; }

    public enum ComplaintStatus {
        PENDING, REVIEWED, RESOLVED, REJECTED, ACCEPTED, VERIFIED
    }
}
