package com.gigshield.controller;

import com.gigshield.dto.PayoutResultDTO;
import com.gigshield.entity.Claim;
import com.gigshield.entity.Policy;
import com.gigshield.entity.Worker;
import com.gigshield.repository.ClaimRepository;
import com.gigshield.repository.PolicyRepository;
import com.gigshield.repository.WorkerRepository;
import com.gigshield.service.payment.PayoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutService payoutService;
    private final ClaimRepository claimRepository;
    private final WorkerRepository workerRepository;
    private final PolicyRepository policyRepository;

    @PostMapping("/claim/{claimId}")
    public ResponseEntity<?> payoutForClaim(@PathVariable Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        if (claim.getStatus() == Claim.ClaimStatus.PAID) {
            return ResponseEntity.badRequest().body("Claim " + claimId + " has already been paid.");
        }
        if (claim.getStatus() == Claim.ClaimStatus.REJECTED) {
            return ResponseEntity.badRequest().body("Claim " + claimId + " was rejected and cannot be paid.");
        }

        PayoutResultDTO result = payoutService.processClaimPayout(claim);
        return ResponseEntity.ok(result);
    }

  
    @PostMapping("/simulate/{workerId}")
    public ResponseEntity<?> simulatePayout(
            @PathVariable Long workerId,
            @RequestParam(defaultValue = "HEAVY_RAIN") String trigger,
            @RequestParam(defaultValue = "350") double payoutAmount) {

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found: " + workerId));

        List<Policy> policies = policyRepository.findByWorkerIdAndStatus(workerId, Policy.PolicyStatus.ACTIVE);
        if (policies.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Worker " + workerId + " has no active policy. Please create a policy first.");
        }
        Policy activePolicy = policies.get(0);

        // Build a mock claim
        Claim claim = new Claim();
        claim.setWorker(worker);
        claim.setPolicy(activePolicy);
        claim.setTriggerType(Claim.TriggerType.valueOf(trigger));
        claim.setObservedValue(42.0);      
        claim.setThresholdValue(35.0);     
        claim.setPayoutPercentage(BigDecimal.valueOf(0.50));
        claim.setPayoutAmount(BigDecimal.valueOf(payoutAmount));
        claim.setTriggeredAt(LocalDateTime.now());
        claim.setStatus(Claim.ClaimStatus.INITIATED);
        claim.setFraudFlagged(false);
        Claim savedClaim = claimRepository.save(claim);

        // Process the payout immediately
        PayoutResultDTO result = payoutService.processClaimPayout(savedClaim);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        return ResponseEntity.ok(claimRepository.findAll());
    }

    @GetMapping("/history/{workerId}")
    public ResponseEntity<?> getPayoutHistory(@PathVariable String workerId) {
        try {
            String cleanIdStr = workerId.split(":")[0].replaceAll("[^0-9]", "");
            if (cleanIdStr.isEmpty()) return ResponseEntity.badRequest().build();
            Long id = Long.parseLong(cleanIdStr);
            
            if (!workerRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            List<Claim> paidClaims = claimRepository.findAll().stream()
                    .filter(c -> c.getWorker() != null && id.equals(c.getWorker().getId()))
                    .filter(c -> c.getStatus() == Claim.ClaimStatus.PAID)
                    .toList();

            return ResponseEntity.ok(paidClaims);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
