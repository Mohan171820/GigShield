package com.gigshield.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Entity
@Table(name = "policies")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(name = "zone")
    private String zone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tier tier; 

   
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePremium;

   
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal dynamicMultiplier;

   
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPremium;

   
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maxWeeklyPayout;

  
    private Integer riskScore;

    @Column(nullable = false)
    private LocalDate weekStartDate;

    @Column(nullable = false)
    private LocalDate weekEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status; 

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = PolicyStatus.ACTIVE;
    }

    public enum Tier {
        BASIC(29, 350),
        STANDARD(59, 700),
        PRO(99, 1200);

        public final int basePremiumRs;
        public final int maxPayoutRs;

        Tier(int basePremiumRs, int maxPayoutRs) {
            this.basePremiumRs = basePremiumRs;
            this.maxPayoutRs = maxPayoutRs;
        }
    }

    public enum PolicyStatus {
        ACTIVE, EXPIRED, CANCELLED
    }
}
