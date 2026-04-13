package com.gigshield.dto;

import com.gigshield.entity.Claim;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AdminStatsDTO {
    private long totalWorkers;
    private long activePolicies;
    private BigDecimal totalPayoutsPaid;
    private BigDecimal totalPremiumsCollected;
    private BigDecimal burningCostRate;
    private BigDecimal lossRatio;
    private List<Claim> recentActivity;

    public long getTotalWorkers() { return totalWorkers; }
    public void setTotalWorkers(long totalWorkers) { this.totalWorkers = totalWorkers; }
    public long getActivePolicies() { return activePolicies; }
    public BigDecimal getTotalPayoutsPaid() { return totalPayoutsPaid; }
}
