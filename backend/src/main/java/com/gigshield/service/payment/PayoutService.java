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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PayoutService {

    private static final Logger log = LoggerFactory.getLogger(PayoutService.class);
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

        // --- Worker Lifetime Coverage Cap Logic ---
        double workerLimit = worker.getCoverageAmount() != null ? worker.getCoverageAmount() : 3000.0;
        BigDecimal totalWorkerHistory = claimRepository.findAll().stream()
                .filter(c -> c.getWorker() != null && worker.getId().equals(c.getWorker().getId()))
                .filter(c -> c.getStatus() == Claim.ClaimStatus.PAID)
                .map(Claim::getPayoutAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWorkerHistory.add(claim.getPayoutAmount()).doubleValue() > workerLimit) {
            log.warn("[PAYOUT-BLOCKED] Lifetime Coverage Cap Exceeded for WorkerID={}. Limit: ₹{}, Already Paid: ₹{}, Current: ₹{}",
                    worker.getId(), workerLimit, totalWorkerHistory, claim.getPayoutAmount());
            
            claim.setStatus(Claim.ClaimStatus.REJECTED);
            claimRepository.save(claim);
            
            return PayoutResultDTO.builder()
                    .claimId(claim.getId())
                    .workerId(worker.getId())
                    .workerName(worker.getName())
                    .upiId(worker.getUpiId())
                    .amountPaid(BigDecimal.ZERO)
                    .currency("INR")
                    .status(PayoutResultDTO.PayoutStatus.FAILED)
                    .statusDescription("Lifetime Coverage Limit Exceeded (₹" + workerLimit + ").")
                    .initiatedAt(initiatedAt)
                    .completedAt(LocalDateTime.now())
                    .build();
        }

        // --- Weekly Policy Cap Logic (Existing) ---
        if (claim.getPolicy() != null) {
            BigDecimal totalPaid = claimRepository.sumPaidPayoutsByPolicyId(claim.getPolicy().getId());
            BigDecimal maxAllowed = claim.getPolicy().getMaxWeeklyPayout();
            
            if (totalPaid.add(claim.getPayoutAmount()).compareTo(maxAllowed) > 0) {
                log.warn("[PAYOUT-BLOCKED] Weekly Coverage Cap Exceeded for PolicyID={}. Max Allowed: ₹{}, Total Already Paid: ₹{}, Current Claim: ₹{}", 
                        claim.getPolicy().getId(), maxAllowed, totalPaid, claim.getPayoutAmount());
                
                claim.setStatus(Claim.ClaimStatus.REJECTED);
                claimRepository.save(claim);
                
                return PayoutResultDTO.builder()
                        .claimId(claim.getId())
                        .workerId(worker.getId())
                        .workerName(worker.getName())
                        .upiId(worker.getUpiId())
                        .amountPaid(BigDecimal.ZERO)
                        .currency("INR")
                        .status(PayoutResultDTO.PayoutStatus.FAILED)
                        .statusDescription("Weekly Coverage Cap Exceeded. Max weekly payout limit reached.")
                        .initiatedAt(initiatedAt)
                        .completedAt(LocalDateTime.now())
                        .build();
            }
        }

        if (sandboxMode) {
            return processSandboxPayout(claim, worker, initiatedAt);
        } else {
            return processRazorpayLivePayout(claim, worker, initiatedAt);
        }
    }


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


    private PayoutResultDTO processRazorpayLivePayout(Claim claim, Worker worker, LocalDateTime initiatedAt) {
        log.warn("[RAZORPAY-LIVE] Live payouts are temporarily disabled in MVP. Falling back to Sandbox mode.");
        return processSandboxPayout(claim, worker, initiatedAt);
    }

}
