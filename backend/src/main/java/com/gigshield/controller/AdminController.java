package com.gigshield.controller;

import com.gigshield.dto.AdminStatsDTO;
import com.gigshield.repository.ClaimRepository;
import com.gigshield.repository.PolicyRepository;
import com.gigshield.repository.WorkerRepository;
import com.gigshield.entity.Policy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

import com.gigshield.repository.ComplaintRepository;
import com.gigshield.service.payment.PayoutService;
import com.gigshield.dto.PayoutResultDTO;
import com.gigshield.entity.Claim;
import com.gigshield.entity.Complaint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final WorkerRepository workerRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final ComplaintRepository complaintRepository;
    private final PayoutService payoutService;

    @PostMapping("/complaints/{id}/approve-payout")
    public ResponseEntity<?> approvePayout(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        
        log.info("[ADMIN-APPROVAL] Attempting payout for complaint {}", id);
        try {
            double amount = Double.parseDouble(String.valueOf(body.get("amount")));

            java.util.Optional<Complaint> optionalComplaint = complaintRepository.findById(id);
            if (optionalComplaint.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Complaint not found"));
            }

            Complaint complaint = optionalComplaint.get();
            
            // Validate status: Must be VERIFIED (ML_VERIFIED) or REVIEWED/ACCEPTED (APPROVED)
            if (complaint.getStatus() == Complaint.ComplaintStatus.RESOLVED) {
                return ResponseEntity.badRequest().body(Map.of("error", "Complaint is already resolved."));
            }
            if (complaint.getStatus() == Complaint.ComplaintStatus.REJECTED) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot approve a rejected complaint."));
            }

            // Find tracking claim or create logic
            Claim claim = claimRepository.findAll().stream()
                    .filter(c -> c.getWorker() != null && c.getWorker().getId().equals(complaint.getWorker().getId()))
                    .filter(c -> c.getStatus() == Claim.ClaimStatus.INITIATED || c.getStatus() == Claim.ClaimStatus.FRAUD_REVIEW)
                    .findFirst()
                    .orElse(null);

            if (claim == null) {
                claim = new Claim();
                claim.setWorker(complaint.getWorker());
                java.util.List<Policy> policies = policyRepository.findByWorkerIdAndStatus(complaint.getWorker().getId(), Policy.PolicyStatus.ACTIVE);
                claim.setPolicy(policies.isEmpty() ? null : policies.get(0));
                claim.setTriggerType(Claim.TriggerType.HEAVY_RAIN); 
                claim.setTriggeredAt(java.time.LocalDateTime.now());
            }

            claim.setPayoutAmount(BigDecimal.valueOf(amount));
            claim.setStatus(Claim.ClaimStatus.INITIATED);
            claimRepository.save(claim);

            PayoutResultDTO payoutResult = payoutService.processClaimPayout(claim);

            if (payoutResult.getStatus() == PayoutResultDTO.PayoutStatus.SUCCESS) {
                complaint.setStatus(Complaint.ComplaintStatus.RESOLVED);
                complaint.setSuggestedPayoutAmount((int) amount);
                complaintRepository.save(complaint);
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "razorpayPayoutId", payoutResult.getRazorpayPayoutId() != null ? payoutResult.getRazorpayPayoutId() : "pout_mock_" + System.currentTimeMillis(),
                    "workerId", complaint.getWorker().getId(),
                    "amount", amount
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", payoutResult.getStatusDescription()));
            }

        } catch (Exception e) {
            log.error("[ADMIN-APPROVAL] Error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if ("admin@gigshield.in".equals(email) && "admin123".equals(password)) {
            return ResponseEntity.ok(Map.of("message", "Admin login successful", "role", "INSURER"));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid admin credentials"));
    }

    @GetMapping({"/stats", "/analytics", "/summary"})
    public ResponseEntity<Map<String, Object>> getPlatformStats() {
        BigDecimal totalPaid = claimRepository.sumTotalPayouts();
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;

        BigDecimal totalPremiums = policyRepository.sumTotalPremiums();
        if (totalPremiums == null) totalPremiums = BigDecimal.ZERO;

       
        BigDecimal demoPremiums = totalPremiums;
        long activePolicies = policyRepository.countByStatus(Policy.PolicyStatus.ACTIVE);
        long totalWorkers = workerRepository.count();
        BigDecimal bcr = BigDecimal.ZERO;
        if (demoPremiums.compareTo(BigDecimal.ZERO) > 0) {
            bcr = totalPaid.multiply(new BigDecimal(100)).divide(demoPremiums, 2, java.math.RoundingMode.HALF_UP);
        }
        double lowRisk = 0.65;
        double medRisk = 0.25; 
        double highRisk = 0.10; 

        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("activePolicies", activePolicies);
        stats.put("active_policies", activePolicies);
        
        stats.put("totalPremiumsCollected", demoPremiums);
        stats.put("total_premiums_collected", demoPremiums);
        stats.put("totalRevenue", demoPremiums);
        
        stats.put("totalPayoutsPaid", totalPaid);
        stats.put("total_payouts_paid", totalPaid);
        stats.put("totalPaid", totalPaid);
        
        stats.put("burningCostRate", bcr);
        stats.put("burning_cost_rate", bcr);
        stats.put("lossRatio", bcr);
        stats.put("loss_ratio", bcr);
        
        stats.put("totalWorkers", totalWorkers);
        stats.put("total_workers", totalWorkers);
        
        stats.put("lowRisk", lowRisk * 100);
        stats.put("mediumRisk", medRisk * 100);
        stats.put("highRisk", highRisk * 100);
        
        stats.put("recentActivity", claimRepository.findTop10ByOrderByCreatedAtDesc());

        return ResponseEntity.ok(stats);
    }
}
