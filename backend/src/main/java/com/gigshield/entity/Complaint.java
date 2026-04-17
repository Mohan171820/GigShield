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
        private String deviceFingerprint;
        private String ipAddress;
        private Boolean sharedDeviceFlag = false;
        private java.util.List<String> riskReasons = new java.util.ArrayList<>();
        private java.time.LocalDateTime policyRegisteredAt;
        private java.time.LocalDateTime createdAt;

        public ComplaintBuilder deviceFingerprint(String f) { this.deviceFingerprint = f; return this; }
        public ComplaintBuilder ipAddress(String i) { this.ipAddress = i; return this; }
        public ComplaintBuilder sharedDeviceFlag(Boolean s) { this.sharedDeviceFlag = s; return this; }
        public ComplaintBuilder riskReasons(java.util.List<String> r) { this.riskReasons = r; return this; }
        public ComplaintBuilder policyRegisteredAt(java.time.LocalDateTime t) { this.policyRegisteredAt = t; return this; }

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
            c.deviceFingerprint = this.deviceFingerprint;
            c.ipAddress = this.ipAddress;
            c.sharedDeviceFlag = this.sharedDeviceFlag != null ? this.sharedDeviceFlag : false;
            c.riskReasons = this.riskReasons != null ? this.riskReasons : new java.util.ArrayList<>();
            c.policyRegisteredAt = this.policyRegisteredAt;
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

    @Column(name = "device_fingerprint")
    private String deviceFingerprint;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "shared_device_flag")
    private Boolean sharedDeviceFlag = false;

    @ElementCollection
    @CollectionTable(name = "complaint_risk_reasons", joinColumns = @JoinColumn(name = "complaint_id"))
    @Column(name = "reason")
    private java.util.List<String> riskReasons = new java.util.ArrayList<>();

    @Column(name = "policy_registered_at")
    private LocalDateTime policyRegisteredAt;

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
    
    public String getDeviceFingerprint() { return deviceFingerprint; }
    public String getIpAddress() { return ipAddress; }
    public Boolean getSharedDeviceFlag() { return sharedDeviceFlag; }
    public java.util.List<String> getRiskReasons() { return riskReasons; }
    public LocalDateTime getPolicyRegisteredAt() { return policyRegisteredAt; }

    public void setDeviceFingerprint(String f) { this.deviceFingerprint = f; }
    public void setIpAddress(String i) { this.ipAddress = i; }
    public void setSharedDeviceFlag(Boolean s) { this.sharedDeviceFlag = s; }
    public void setRiskReasons(java.util.List<String> r) { this.riskReasons = r; }
    public void setPolicyRegisteredAt(LocalDateTime t) { this.policyRegisteredAt = t; }

    public enum ComplaintStatus {
        PENDING, REVIEWED, RESOLVED, REJECTED, ACCEPTED, VERIFIED
    }
}
