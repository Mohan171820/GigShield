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

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final WorkerRepository workerRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;

    /**
     * Admin Login: Uses demo credentials from UI.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if ("admin@gigshield.in".equals(email) && "admin123".equals(password)) {
            return ResponseEntity.ok(Map.of("message", "Admin login successful", "role", "INSURER"));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid admin credentials"));
    }

    /**
     * Platform Analytics: Aggregates stats for the dashboard.
     * Supports multiple aliases for the dashboard's analytics engine.
     */
    @GetMapping({"/stats", "/analytics", "/summary"})
    public ResponseEntity<Map<String, Object>> getPlatformStats() {
        BigDecimal totalPaid = claimRepository.sumTotalPayouts();
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;

        BigDecimal totalPremiums = policyRepository.sumTotalPremiums();
        if (totalPremiums == null) totalPremiums = BigDecimal.ZERO;

        // ─────────────────────────────────────────────────────────────────
        // 🧪 ACTUAL ACTUARIAL DATA
        // Data is now balanced in data.sql to naturally target BCR ~0.65
        // ─────────────────────────────────────────────────────────────────
        BigDecimal demoPremiums = totalPremiums;


        long activePolicies = policyRepository.countByStatus(Policy.PolicyStatus.ACTIVE);
        long totalWorkers = workerRepository.count();

        // Calculate BCR & Loss Ratio (as a percentage 0-100 for the UI)
        BigDecimal bcr = BigDecimal.ZERO;
        if (demoPremiums.compareTo(BigDecimal.ZERO) > 0) {
            bcr = totalPaid.multiply(new BigDecimal(100)).divide(demoPremiums, 2, java.math.RoundingMode.HALF_UP);
        }

        // Mock/Calculate Risk Distribution for the UI
        double lowRisk = 0.65; // e.g., 65% low
        double medRisk = 0.25; // e.g., 25% med
        double highRisk = 0.10; // e.g., 10% high

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
