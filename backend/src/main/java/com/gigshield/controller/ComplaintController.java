package com.gigshield.controller;

import com.gigshield.dto.ClaimPredictionResponseDTO;
import com.gigshield.dto.FeatureRequestDTO;
import com.gigshield.entity.Claim;
import com.gigshield.entity.Complaint;
import com.gigshield.entity.Policy;
import com.gigshield.entity.Worker;
import com.gigshield.repository.ClaimRepository;
import com.gigshield.repository.ComplaintRepository;
import com.gigshield.repository.PolicyRepository;
import com.gigshield.repository.WorkerRepository;
import com.gigshield.service.MLDataService;
import com.gigshield.service.XGBoostInferenceService;
import com.gigshield.service.payment.PayoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller to handle manual disruption reports (Complaints) from workers.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final WorkerRepository workerRepository;
    private final PolicyRepository policyRepository;
    private final ClaimRepository claimRepository;
    private final MLDataService mlDataService;
    private final XGBoostInferenceService inferenceService;
    private final PayoutService payoutService;

    /**
     * Fetches ALL complaints on the platform for the Admin Dashboard.
     * Supports both the root path and the /all alias.
     */
    @GetMapping(value = {"", "/all"})
    public ResponseEntity<List<Map<String, Object>>> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        // Flatten worker ID for the frontend dashboard
        List<Map<String, Object>> result = complaints.stream().map(c -> {
            Map<String, Object> map = new java.util.HashMap<>(Map.of(
                "id", c.getId(),
                "category", c.getCategory(),
                "description", c.getDescription() != null ? c.getDescription() : "",
                "status", c.getStatus(),
                "createdAt", c.getCreatedAt()
            ));
            if (c.getWorker() != null) map.put("workerId", c.getWorker().getId());
            return map;
        }).toList();
        return ResponseEntity.ok(result);
    }

    /**
     * ML AUTO-PILOT: Performs an on-demand ML verification for a specific complaint.
     * Path: /api/complaints/verify-complaint/{id}
     */
    @GetMapping("/verify-complaint/{id}")
    public ResponseEntity<?> verifyComplaintAutoPilot(@PathVariable String id) {
        log.info("[ML-AUTO-PILOT] Requesting decision for Complaint ID: {}", id);
        try {
            Long cleanWorkerId = parseWorkerId(id);
            FeatureRequestDTO features = mlDataService.aggregateFeaturesForWorker(cleanWorkerId);
            ClaimPredictionResponseDTO mlResult = inferenceService.predictClaimEligibility(features);
            
            return ResponseEntity.ok(Map.of(
                "eligible", mlResult.isEligible(),
                "claim_amount", mlResult.getClaim_amount(),
                "message", mlResult.getMessage(),
                "confidence", mlResult.getConfidence()
            ));
        } catch (Exception e) {
            log.error("[ML-AUTO-PILOT] Error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Updates the status of a complaint. 
     * Frontend uses: GET /api/v1/complaints/{id}?status=ACCEPTED
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> updateStatusViaGet(
            @PathVariable Long id, 
            @RequestParam(name = "status") String rawStatus) {
        
        log.info("[ADMIN-GET-STATUS] Received status update via GET for ID={}: '{}'", id, rawStatus);
        return processStatusUpdate(id, rawStatus);
    }

    /**
     * Updates the status of a complaint (Accept/Reject).
     * Supports PUT/POST for future-proofing.
     */
    @RequestMapping(value = "/{id}/status", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id, 
            @RequestParam(name = "status", required = false) String statusParam,
            @RequestBody(required = false) Map<String, String> body) {
        
        String rawStatus = (statusParam != null) ? statusParam : (body != null ? body.get("status") : null);
        return processStatusUpdate(id, rawStatus);
    }

    private ResponseEntity<?> processStatusUpdate(Long id, String rawStatus) {
        if (rawStatus == null || rawStatus.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status is required."));
        }

        try {
            String mappedStatusStr = rawStatus.toUpperCase();
            if (mappedStatusStr.equals("ACCEPT") || mappedStatusStr.equals("APPROVED") || mappedStatusStr.equals("ACCEPTED")) mappedStatusStr = "RESOLVED";
            if (mappedStatusStr.equals("REJECT") || mappedStatusStr.equals("REJECTED")) mappedStatusStr = "REJECTED";

            final Complaint.ComplaintStatus finalStatus = Complaint.ComplaintStatus.valueOf(mappedStatusStr);

            return complaintRepository.findById(id)
                    .map(c -> {
                        c.setStatus(finalStatus);
                        complaintRepository.save(c);
                        log.info("[ADMIN-STATUS] ✅ SUCCESS: Complaint ID={} set to {}", id, finalStatus);
                        return ResponseEntity.ok(Map.of("message", "Status updated successfully to " + finalStatus));
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (IllegalArgumentException e) {
            log.error("[ADMIN-STATUS] ❌ INVALID status: '{}'", rawStatus);
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status: " + rawStatus));
        }
    }

    /**
     * Submits a manual complaint/report.
     * Includes robust parsing for workerId strings like '4:1'.
     */
    @PostMapping
    public ResponseEntity<?> submitComplaint(@RequestBody Map<String, Object> payload) {
        try {
            String workerIdStr = String.valueOf(payload.get("workerId"));
            String category = (String) payload.get("category");
            String description = (String) payload.get("description");

            if (category == null) return ResponseEntity.badRequest().body("Category is required.");

            Long cleanWorkerId = parseWorkerId(workerIdStr);
            Worker worker = workerRepository.findById(cleanWorkerId)
                    .orElseThrow(() -> new RuntimeException("Worker not found: " + cleanWorkerId));

            Complaint complaint = Complaint.builder()
                    .worker(worker)
                    .category(category)
                    .description(description)
                    .status(Complaint.ComplaintStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            Complaint saved = complaintRepository.save(complaint);
            log.info("Complaint submitted: ID={}, WorkerID={}", saved.getId(), cleanWorkerId);

            // ─────────────────────────────────────────────────────────────────
            // 🤖 ML AUTO-PILOT: Automatic Approval or Rejection
            // ─────────────────────────────────────────────────────────────────
            try {
                log.info("[ML-AUTO-PILOT] Initiating parametric verification for WorkerID={}", cleanWorkerId);
                
                // 1. Get current 7 weather/AQI features for the worker's city
                FeatureRequestDTO features = mlDataService.aggregateFeaturesForWorker(cleanWorkerId);
                
                // 2. Query the remote FastAPI XGBoost model
                ClaimPredictionResponseDTO mlResult = inferenceService.predictClaimEligibility(features);
                
                if (mlResult.isEligible()) {
                    log.info("[ML-AUTO-PILOT] ✅ Conditions met! Triggering automatic payout of ₹{}", mlResult.getClaim_amount());
                    
                    List<Policy> policies = policyRepository.findByWorkerIdAndStatus(cleanWorkerId, Policy.PolicyStatus.ACTIVE);
                    if (!policies.isEmpty()) {
                        Policy activePolicy = policies.get(0);
                        
                        // Create and link the Claim record
                        Claim claim = new Claim();
                        claim.setWorker(worker);
                        claim.setPolicy(activePolicy);
                        try {
                            claim.setTriggerType(Claim.TriggerType.valueOf(category.toUpperCase().replace(" ", "_")));
                        } catch (Exception e) {
                            claim.setTriggerType(Claim.TriggerType.HEAVY_RAIN);
                        }
                        claim.setObservedValue(features.getRain_mm() > 0 ? features.getRain_mm() : 35.0);
                        claim.setThresholdValue(35.0);
                        claim.setPayoutAmount(BigDecimal.valueOf(mlResult.getClaim_amount()));
                        claim.setTriggeredAt(LocalDateTime.now());
                        claim.setStatus(Claim.ClaimStatus.PAID); // Map directly to PAID if ML approves
                        
                        Claim savedClaim = claimRepository.save(claim);
                        payoutService.processClaimPayout(savedClaim);
                        
                        // AUTO-RESOLVE the complaint
                        saved.setStatus(Complaint.ComplaintStatus.RESOLVED);
                        complaintRepository.save(saved);
                        
                        return ResponseEntity.ok(Map.of(
                            "id", saved.getId(),
                            "autoPaid", true,
                            "payoutAmount", mlResult.getClaim_amount(),
                            "message", "✅ Automatic Approval: Conditions verified! Payout of ₹" + mlResult.getClaim_amount() + " sent via UPI."
                        ));
                    }
                } else if ("success".equalsIgnoreCase(mlResult.getStatus())) {
                    // Logic for automatic rejection!
                    log.info("[ML-AUTO-PILOT] ❌ Conditions not met. Auto-rejecting Complaint ID: {}", saved.getId());
                    saved.setStatus(Complaint.ComplaintStatus.REJECTED);
                    complaintRepository.save(saved);
                    
                    return ResponseEntity.ok(Map.of(
                        "id", saved.getId(),
                        "autoPaid", false,
                        "autoRejected", true,
                        "message", "❌ Automatic Check Failed: Our ML sensors at " + worker.getCity() + " show conditions do not meet payout criteria. Report auto-rejected."
                    ));
                }
            } catch (Exception mlEx) {
                log.warn("[ML-AUTO-PILOT] ⚠️ Verification offline: {}. Defaulting to PENDING for manual review.", mlEx.getMessage());
                // Fallback: The complaint remains PENDING as initialized on line 164
            }

            return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "autoPaid", false,
                "message", "Report submitted successfully. Standing by for manual verification as ML system is recalibrating."
            ));
        } catch (Exception e) {
            log.error("Failed to submit complaint: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Retrieves the complaint history for a worker.
     * Includes robust parsing for workerId strings like '4:1'.
     */
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<Complaint>> getWorkerComplaintHistory(@PathVariable String workerId) {
        try {
            Long cleanId = parseWorkerId(workerId);
            return ResponseEntity.ok(complaintRepository.findByWorkerIdOrderByCreatedAtDesc(cleanId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Smart internal utility to handle IDs formatted like '4:1' or '4' or ':1'.
     */
    private Long parseWorkerId(String idStr) {
        if (idStr == null || idStr.isEmpty()) throw new IllegalArgumentException("Invalid ID");
        // Split by colon and take the first part, then strip non-digits
        String clean = idStr.split(":")[0].replaceAll("[^0-9]", "");
        if (clean.isEmpty()) throw new IllegalArgumentException("No numeric ID found in: " + idStr);
        return Long.parseLong(clean);
    }
}
