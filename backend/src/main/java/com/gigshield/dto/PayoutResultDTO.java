package com.gigshield.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Result returned after a payout attempt.
 * Contains the Razorpay transaction ID, status, and payout details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutResultDTO {

    private Long claimId;
    private Long workerId;
    private String workerName;
    private String upiId;
    private BigDecimal amountPaid;
    private String currency;
    private String razorpayPayoutId;       // Razorpay payout reference ID
    private String razorpayFundAccountId;  // Razorpay fund account reference
    private PayoutStatus status;
    private String statusDescription;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private boolean sandboxMode;

    public enum PayoutStatus {
        SUCCESS,
        FAILED,
        PENDING
    }
}
