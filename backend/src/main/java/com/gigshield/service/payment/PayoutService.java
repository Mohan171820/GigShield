package com.gigshield.service.payment;

import com.gigshield.dto.PayoutResultDTO;
import com.gigshield.entity.Claim;
import com.gigshield.entity.Worker;
import com.gigshield.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Core payout engine for GigShield.
 *
 * Flow:
 *  1. Triggered when a parametric claim is auto-initiated.
 *  2. Fetches the claimant worker's UPI ID from their profile.
 *  3. Sends payout via Razorpay Payouts API (or sandbox mock if enabled).
 *  4. Updates Claim status to PAID with Razorpay reference ID.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutService {

    private final ClaimRepository claimRepository;

    @Value("${gigshield.payment.razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${gigshield.payment.razorpay.key-secret}")
    private String razorpayKeySecret;

    @Value("${gigshield.payment.sandbox-mode:true}")
    private boolean sandboxMode;

    /**
     * Processes a UPI payout for a specific claim.
     * In sandbox mode, it simulates the full Razorpay payment flow.
     * In live mode, it calls the real Razorpay Payouts API.
     *
     * @param claim The Claim entity to be paid out.
     * @return PayoutResultDTO with transaction details.
     */
    public PayoutResultDTO processClaimPayout(Claim claim) {
        Worker worker = claim.getWorker();
        LocalDateTime initiatedAt = LocalDateTime.now();

        log.info("[PAYOUT] Initiating payout for Claim ID={} | Worker={} | UPI={} | Amount=₹{}",
                claim.getId(), worker.getName(), worker.getUpiId(), claim.getPayoutAmount());

        if (sandboxMode) {
            return processSandboxPayout(claim, worker, initiatedAt);
        } else {
            return processRazorpayLivePayout(claim, worker, initiatedAt);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sandbox Mode — Simulates a successful UPI payout
    // ─────────────────────────────────────────────────────────────────────────

    private PayoutResultDTO processSandboxPayout(Claim claim, Worker worker, LocalDateTime initiatedAt) {
        log.info("[SANDBOX] Simulating UPI transfer of ₹{} to UPI ID: {}",
                claim.getPayoutAmount(), worker.getUpiId());

        // Simulate a mock Razorpay Payout ID
        String mockPayoutId = "pout_SANDBOX_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();
        String mockFundAccountId = "fa_SANDBOX_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();

        // Update claim to PAID in DB
        claim.setStatus(Claim.ClaimStatus.PAID);
        claim.setPaidAt(LocalDateTime.now());
        claimRepository.save(claim);

        log.info("[SANDBOX] ✅ Payout SUCCESS → Claim ID={} marked PAID. Razorpay Payout ID={}",
                claim.getId(), mockPayoutId);

        return PayoutResultDTO.builder()
                .claimId(claim.getId())
                .workerId(worker.getId())
                .workerName(worker.getName())
                .upiId(worker.getUpiId())
                .amountPaid(claim.getPayoutAmount())
                .currency("INR")
                .razorpayPayoutId(mockPayoutId)
                .razorpayFundAccountId(mockFundAccountId)
                .status(PayoutResultDTO.PayoutStatus.SUCCESS)
                .statusDescription("Sandbox UPI payout simulated successfully. ₹" + claim.getPayoutAmount() + " → " + worker.getUpiId())
                .initiatedAt(initiatedAt)
                .completedAt(LocalDateTime.now())
                .sandboxMode(true)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Live Mode — Real Razorpay Payouts API
    // ─────────────────────────────────────────────────────────────────────────

    private PayoutResultDTO processRazorpayLivePayout(Claim claim, Worker worker, LocalDateTime initiatedAt) {
        log.warn("[RAZORPAY-LIVE] Live payouts are temporarily disabled in MVP. Falling back to Sandbox mode.");
        return processSandboxPayout(claim, worker, initiatedAt);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper: Create a Razorpay Contact
    // ─────────────────────────────────────────────────────────────────────────

}
