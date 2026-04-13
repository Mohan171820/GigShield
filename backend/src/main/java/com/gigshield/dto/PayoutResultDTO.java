package com.gigshield.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public PayoutResultDTO() {}

    public PayoutResultDTO(Long claimId, Long workerId, String workerName, String upiId, 
                          BigDecimal amountPaid, String currency, String razorpayPayoutId, 
                          String razorpayFundAccountId, PayoutStatus status, 
                          String statusDescription, LocalDateTime initiatedAt, 
                          LocalDateTime completedAt, boolean sandboxMode) {
        this.claimId = claimId;
        this.workerId = workerId;
        this.workerName = workerName;
        this.upiId = upiId;
        this.amountPaid = amountPaid;
        this.currency = currency;
        this.razorpayPayoutId = razorpayPayoutId;
        this.razorpayFundAccountId = razorpayFundAccountId;
        this.status = status;
        this.statusDescription = statusDescription;
        this.initiatedAt = initiatedAt;
        this.completedAt = completedAt;
        this.sandboxMode = sandboxMode;
    }

    public static PayoutResultDTOBuilder builder() {
        return new PayoutResultDTOBuilder();
    }

    public static class PayoutResultDTOBuilder {
        private Long cId;
        private Long wId;
        private String wName;
        private String upi;
        private BigDecimal amt;
        private String cur;
        private String rpPayoutId;
        private String rpFundId;
        private PayoutStatus st;
        private String desc;
        private LocalDateTime init;
        private LocalDateTime comp;
        private boolean sand;

        public PayoutResultDTOBuilder claimId(Long v) { this.cId = v; return this; }
        public PayoutResultDTOBuilder workerId(Long v) { this.wId = v; return this; }
        public PayoutResultDTOBuilder workerName(String v) { this.wName = v; return this; }
        public PayoutResultDTOBuilder upiId(String v) { this.upi = v; return this; }
        public PayoutResultDTOBuilder amountPaid(BigDecimal v) { this.amt = v; return this; }
        public PayoutResultDTOBuilder currency(String v) { this.cur = v; return this; }
        public PayoutResultDTOBuilder razorpayPayoutId(String v) { this.rpPayoutId = v; return this; }
        public PayoutResultDTOBuilder razorpayFundAccountId(String v) { this.rpFundId = v; return this; }
        public PayoutResultDTOBuilder status(PayoutStatus v) { this.st = v; return this; }
        public PayoutResultDTOBuilder statusDescription(String v) { this.desc = v; return this; }
        public PayoutResultDTOBuilder initiatedAt(LocalDateTime v) { this.init = v; return this; }
        public PayoutResultDTOBuilder completedAt(LocalDateTime v) { this.comp = v; return this; }
        public PayoutResultDTOBuilder sandboxMode(boolean v) { this.sand = v; return this; }

        public PayoutResultDTO build() {
            return new PayoutResultDTO(cId, wId, wName, upi, amt, cur, rpPayoutId, rpFundId, st, desc, init, comp, sand);
        }
    }

    public PayoutStatus getStatus() { return status; }
    public String getStatusDescription() { return statusDescription; }
    public String getRazorpayPayoutId() { return razorpayPayoutId; }

    public enum PayoutStatus {
        SUCCESS,
        FAILED,
        PENDING
    }
}
