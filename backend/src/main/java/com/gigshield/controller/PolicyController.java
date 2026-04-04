package com.gigshield.controller;

import com.gigshield.dto.FeatureRequestDTO;
import com.gigshield.dto.RegistrationRequestDTO;
import com.gigshield.entity.Policy;
import com.gigshield.entity.Worker;
import com.gigshield.repository.PolicyRepository;
import com.gigshield.repository.WorkerRepository;
import com.gigshield.service.MLDataService;
import com.gigshield.service.XGBoostInferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyRepository policyRepository;
    private final WorkerRepository workerRepository;
    private final com.gigshield.repository.ClaimRepository claimRepository;
    private final MLDataService mlDataService;
    private final XGBoostInferenceService inferenceService;
    @PostMapping(value = {"/register-full", ""}) 
    public ResponseEntity<?> registerFull(@RequestBody RegistrationRequestDTO request) {
        try {
            log.info("Full live registration request: [Name: {}, Platform: {}, Zone: {}]", 
                request.getName(), request.getPlatform(), request.getZone());
            log.debug("DEBUG - Full Request DTO: {}", request);
            BigDecimal totalPaid = claimRepository.sumTotalPayouts();
            BigDecimal totalPremiums = policyRepository.sumTotalPremiums();
            if (totalPaid != null && totalPremiums != null && totalPremiums.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal lossRatio = totalPaid.divide(totalPremiums, 4, java.math.RoundingMode.HALF_UP);
                if (lossRatio.compareTo(new BigDecimal("0.85")) > 0) {
                    return ResponseEntity.status(403).body(java.util.Map.of(
                        "error", "Enrolments Suspended",
                        "details", "The platform is currently at high risk (Loss Ratio > 85%). New policies are temporarily paused."
                    ));
                }
            }
            Worker worker = null;
            if (request.getWorkerId() != null) {
                worker = workerRepository.findById(request.getWorkerId()).orElse(null);
            }
            if (worker == null && request.getPhoneNumber() != null) {
                worker = workerRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);
            }
            if (worker == null) {
                if (request.getName() == null || request.getPhoneNumber() == null) {
                    return ResponseEntity.badRequest().body(java.util.Map.of("error", "Name and Phone Number are required for new workers."));
                }
                worker = new Worker();
            }
            
            // Only update fields if they were provided in this specific request step
            if (request.getName() != null) worker.setName(request.getName());
            if (request.getPhoneNumber() != null) worker.setPhoneNumber(request.getPhoneNumber());
            if (request.getCity() != null) worker.setCity(request.getCity());
            if (request.getUpiId() != null) worker.setUpiId(request.getUpiId());
            if (request.getPassword() != null) worker.setPassword(request.getPassword()); 
            
            if (request.getZone() != null) {
                worker.setZone(request.getZone());
            } else if (worker.getZone() == null) {
                worker.setZone(worker.getCity() != null ? worker.getCity() : "Unspecified");
            }

            if (request.getWeeklyActiveHours() != null) worker.setWeeklyActiveHours(request.getWeeklyActiveHours());
            else if (worker.getWeeklyActiveHours() == null) worker.setWeeklyActiveHours(35.0);

            if (request.getTenureWeeks() != null) worker.setTenureWeeks(request.getTenureWeeks());
            else if (worker.getTenureWeeks() == null) worker.setTenureWeeks(Math.max(1, (int)(Math.random() * 52))); // Demo simulation
            
            if (request.getOrdersThisMonth() != null) worker.setOrdersThisMonth(request.getOrdersThisMonth());
            else if (worker.getOrdersThisMonth() == null) worker.setOrdersThisMonth((int)(80 + Math.random() * 100)); // Demo simulation

            if (request.getAvgDailyEarnings() != null) worker.setAvgDailyEarnings(request.getAvgDailyEarnings());
            else if (worker.getAvgDailyEarnings() == null) worker.setAvgDailyEarnings(Math.round((450.0 + Math.random() * 300) * 100.0) / 100.0); // Demo simulation
            
            // Final Database Constraint safety
            if (worker.getCity() == null) worker.setCity("Unspecified");
            if (worker.getName() == null) worker.setName("Gig Worker");
            
            if (request.getPlatform() != null) {
                try {
                    worker.setPlatform(Worker.Platform.valueOf(request.getPlatform().toUpperCase().replace(" ", "_")));
                } catch (Exception e) {
                    worker.setPlatform(Worker.Platform.BLINKIT); 
                }
            } else {
                worker.setPlatform(Worker.Platform.BLINKIT);
            }
            
            Worker savedWorker = workerRepository.save(worker);

            // 2. Prepare ML Features & Calculate Risk
            FeatureRequestDTO features = mlDataService.aggregateFeaturesForWorker(savedWorker.getId());
            float riskMultiplier = inferenceService.predictRiskScore(features);

            // 3. SUSTAINABLE PRICING LOGIC
            // Formula: (Trigger Prob) * (Avg Income Lost/Day) * (7 Days)
            // ML score is usually 0.8 - 2.5 representing risk. Let's map it to a probability 0.01 - 0.10
            double prob = (riskMultiplier / 20.0); 
            double avgIncomeLostPerDay = 600.0;
            double baseCalc = prob * avgIncomeLostPerDay * 7;
            
            // Clamp within the target range: ₹20 - ₹50
            if (baseCalc < 20) baseCalc = 21.50; 
            if (baseCalc > 50) baseCalc = 49.99; 
            
            BigDecimal dynamicPremium = BigDecimal.valueOf(baseCalc).setScale(2, java.math.RoundingMode.HALF_UP);
            
            Policy.Tier tier;
            try {
                String rawTier = (request.getTier() != null) ? request.getTier().toUpperCase() : "STANDARD";
                // Smart Aliasing for the frontend
                if (rawTier.equals("PREMIUM") || rawTier.equals("GOLD")) rawTier = "PRO";
                if (rawTier.equals("SILVER")) rawTier = "STANDARD";
                
                tier = Policy.Tier.valueOf(rawTier);
                log.info("[REGISTRATION] Mapped frontend tier '{}' to backend enum '{}'", request.getTier(), tier);
            } catch (Exception e) {
                log.warn("[REGISTRATION] Unrecognized Tier '{}'. Defaulting to STANDARD.", request.getTier());
                tier = Policy.Tier.STANDARD;
            }
            
            Policy policy = new Policy();
            policy.setWorker(savedWorker);
            policy.setTier(tier);
            policy.setZone(request.getZone());
            policy.setBasePremium(dynamicPremium);
            policy.setDynamicMultiplier(BigDecimal.valueOf(riskMultiplier));
            policy.setFinalPremium(dynamicPremium); // Pricing model sets the fixed final price
            policy.setMaxWeeklyPayout(BigDecimal.valueOf(tier.maxPayoutRs));
            policy.setRiskScore((int)(riskMultiplier * 100));
            policy.setWeekStartDate(LocalDate.now());
            policy.setWeekEndDate(LocalDate.now().plusDays(7));
            policy.setStatus(Policy.PolicyStatus.ACTIVE);
            
            Policy savedPolicy = policyRepository.save(policy);

            return ResponseEntity.ok(java.util.Map.of(
                "workerId", savedWorker.getId(),
                "policyId", savedPolicy.getId(),
                "riskScore", policy.getRiskScore(),
                "finalPremium", policy.getFinalPremium(),
                "zone", policy.getZone() != null ? policy.getZone() : "Unspecified",
                "message", "Shield activated! Registration complete."
            ));
        } catch (Exception e) {
            log.error("CRITICAL: Failed to register worker/policy. Error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                "error", "Something went wrong during registration.",
                "details", e.getMessage()
            ));
        }
    }

    @GetMapping({"/worker/{workerId}/active", "/worker/{workerId}"})
    public ResponseEntity<?> getActivePolicy(@PathVariable String workerId) {
        try {
            // Handle cases where frontend might append junk characters like ':1'
            String cleanIdStr = workerId.split(":")[0].replaceAll("[^0-9]", "");
            if (cleanIdStr.isEmpty()) return ResponseEntity.badRequest().build();
            Long id = Long.parseLong(cleanIdStr);

            List<Policy> activePolicies = policyRepository.findByWorkerIdAndStatus(id, Policy.PolicyStatus.ACTIVE);
            if (activePolicies.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Policy p = activePolicies.get(0);
            java.util.Map<String, Object> responseMap = new java.util.HashMap<>();
            responseMap.put("id", p.getId());
            responseMap.put("tier", p.getTier());
            responseMap.put("basePremium", p.getBasePremium());
            responseMap.put("base_premium", p.getBasePremium());
            responseMap.put("dynamicMultiplier", p.getDynamicMultiplier());
            responseMap.put("dynamic_multiplier", p.getDynamicMultiplier());
            responseMap.put("finalPremium", p.getFinalPremium());
            responseMap.put("final_premium", p.getFinalPremium());
            responseMap.put("maxWeeklyPayout", p.getMaxWeeklyPayout());
            responseMap.put("max_weekly_payout", p.getMaxWeeklyPayout());
            responseMap.put("riskScore", p.getRiskScore());
            responseMap.put("risk_score", p.getRiskScore());
            responseMap.put("weekStartDate", p.getWeekStartDate());
            responseMap.put("week_start_date", p.getWeekStartDate());
            responseMap.put("weekEndDate", p.getWeekEndDate());
            responseMap.put("week_end_date", p.getWeekEndDate());
            responseMap.put("status", p.getStatus());
            responseMap.put("zone", p.getZone());

            if (p.getWorker() != null) {
                Worker w = p.getWorker();
                java.util.Map<String, Object> workerMap = new java.util.HashMap<>();
                workerMap.put("id", w.getId());
                workerMap.put("name", w.getName());
                workerMap.put("phone_number", w.getPhoneNumber());
                workerMap.put("phoneNumber", w.getPhoneNumber());
                workerMap.put("city", w.getCity());
                workerMap.put("zone", w.getZone());
                workerMap.put("platform", w.getPlatform());
                workerMap.put("weekly_active_hours", w.getWeeklyActiveHours());
                workerMap.put("weeklyActiveHours", w.getWeeklyActiveHours());
                workerMap.put("tenure_weeks", w.getTenureWeeks());
                workerMap.put("tenureWeeks", w.getTenureWeeks());
                
                // On-the-fly simulation for older profiles created before the update
                Integer orders = w.getOrdersThisMonth() != null ? w.getOrdersThisMonth() : 124;
                Double earnings = w.getAvgDailyEarnings() != null ? w.getAvgDailyEarnings() : 680.50;
                
                workerMap.put("orders_this_month", orders);
                workerMap.put("ordersThisMonth", orders);
                workerMap.put("completed_deliveries", orders); // UI Alias
                
                workerMap.put("avg_daily_earnings", earnings);
                workerMap.put("avgDailyEarnings", earnings);
                workerMap.put("daily_avg", earnings); // UI Alias
                workerMap.put("avg_earnings", earnings); // UI Alias
                
                // Nest worker and also spread flat for max compatibility
                responseMap.put("worker", workerMap);
                responseMap.putAll(workerMap);
            }

            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        return policyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/worker/{workerId}/history")
    public ResponseEntity<List<Policy>> getPolicyHistory(@PathVariable Long workerId) {
        if (workerId == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(policyRepository.findAll().stream()
                .filter(p -> p.getWorker() != null && workerId.equals(p.getWorker().getId()))
                .toList());
    }
}
