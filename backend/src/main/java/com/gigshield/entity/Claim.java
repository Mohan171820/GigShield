package com.gigshield.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
@Table(name = "payouts")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = true)
    private Policy policy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TriggerType triggerType;

    @Column(nullable = true)
    private Double observedValue;

    @Column(nullable = true)
    private Double thresholdValue;

    @Column(nullable = true, precision = 5, scale = 4)
    private BigDecimal payoutPercentage;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal payoutAmount;

    @Column(nullable = true)
    private LocalDateTime triggeredAt;

    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status; 

    private Boolean fraudFlagged = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Frontend compatibility getters
    @com.fasterxml.jackson.annotation.JsonProperty("amount")
    public BigDecimal getAmountForJson() {
        return payoutAmount;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("workerId")
    public Long getWorkerIdForJson() {
        return worker != null ? worker.getId() : null;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("transferId")
    public String getTransferIdForJson() {
        return "txn_" + id; // Mock standard transfer ID format
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = ClaimStatus.INITIATED;
    }

    public enum TriggerType {
        HEAVY_RAIN,        
        EXTREME_RAIN,      
        SEVERE_AQI,        
        EXTREME_HEAT,      
        CYCLONE_ALERT,    
        SEVERE_STORM,      
        STREET_BLOCKAGE,  
        CURFEW_SHUTDOWN,   
        PLATFORM_OUTAGE  ;  
    }

    public enum ClaimStatus {
        INITIATED, FRAUD_REVIEW, PAID, REJECTED
    }
}
