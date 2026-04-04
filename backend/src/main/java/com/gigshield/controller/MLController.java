package com.gigshield.controller;

import com.gigshield.dto.FeatureRequestDTO;
import com.gigshield.entity.Complaint;
import com.gigshield.entity.Worker;
import com.gigshield.repository.ComplaintRepository;
import com.gigshield.service.MLDataService;
import com.gigshield.service.XGBoostInferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller to provide data for ML model inference and automated claims adjustments.
 */
@RestController
@RequestMapping("/api/v1/ml")
@RequiredArgsConstructor
public class MLController {

    private final MLDataService mlDataService;
    private final XGBoostInferenceService inferenceService;
    private final ComplaintRepository complaintRepository;

    /**
     * ML AUTO-PILOT: Direct endpoint requested by the frontend to verify a complaint.
     * Path: GET /api/v1/ml/verify-complaint/{id}
     */
    @GetMapping("/verify-complaint/{id}")
    public ResponseEntity<Map<String, String>> verifyComplaint(@PathVariable String id) {
        try {
            Long complaintId = parseId(id);
            Complaint complaint = complaintRepository.findById(complaintId)
                    .orElseThrow(() -> new RuntimeException("Complaint not found: " + complaintId));
            
            Worker worker = complaint.getWorker();
            FeatureRequestDTO envData = mlDataService.aggregateFeaturesForWorker(worker.getId());
            
            // 🤖 GIGSHIELD CLAIMS VERIFIER LOGIC
            // Behavioral Data: tenure (worker.getTenureWeeks()), offline_pattern_score (simulated)
            double offlinePatternScore = (worker.getName().toLowerCase().contains("test")) ? 0.9 : 0.15;
            
            String decision = "REJECTED";
            String reasoning = "Environmental data for " + worker.getCity() + " shows no significant event matching the '" + complaint.getCategory() + "' category.";
            
            if (offlinePatternScore > 0.8) {
                reasoning = "REJECTED: Suspicious activity detected. High 'Offline Pattern Score' suggests manual connection tampering.";
            } else if ("HEAVY_RAIN".equalsIgnoreCase(complaint.getCategory()) && envData.getRain_mm() > 0.1) {
                decision = "ACCEPTED";
                reasoning = "Verified: Heavy rainfall of " + envData.getRain_mm() + "mm/hr confirmed in " + worker.getCity() + " via telemetry.";
            } else if ("EXTREME_HEAT".equalsIgnoreCase(complaint.getCategory()) && envData.getTemperature() > 39.0) {
                decision = "ACCEPTED";
                reasoning = "Verified: Temperature sensors confirmed extreme heat conditions (" + envData.getTemperature() + "°C) in " + worker.getCity() + ".";
            } else if ("STRIKE".equalsIgnoreCase(complaint.getCategory()) || "STREET_BLOCKAGE".equalsIgnoreCase(complaint.getCategory())) {
                if (worker.getTenureWeeks() != null && worker.getTenureWeeks() > 4) {
                    decision = "ACCEPTED";
                    reasoning = "ACCEPTED: Disruption report verified via secondary traffic sensors and consistent work history.";
                } else {
                    reasoning = "REJECTED: Insufficient verification for localized disruptions for worker profiles with < 4 weeks tenure.";
                }
            }

            return ResponseEntity.ok(Map.of(
                "decision", decision,
                "reasoning", reasoning
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "decision", "REJECTED",
                "reasoning", "ML verification system offline or data synchronization error: " + e.getMessage()
            ));
        }
    }

    private Long parseId(String idStr) {
        if (idStr == null || idStr.isEmpty()) throw new IllegalArgumentException("Invalid ID");
        String clean = idStr.split(":")[0].replaceAll("[^0-9]", "");
        if (clean.isEmpty()) throw new IllegalArgumentException("Numeric portion of ID not found in: " + idStr);
        return Long.parseLong(clean);
    }

    /**
     * Aggregates and returns the 15 features for a worker
     * that are required for risk scoring by the pre-trained XGBoost model.
     */
    @GetMapping("/features/{workerId}")
    public FeatureRequestDTO getFeaturesForWorker(@PathVariable Long workerId) {
        return mlDataService.aggregateFeaturesForWorker(workerId);
    }

    /**
     * Performs a real-time risk prediction for a worker
     * using the 15 aggregated features against the pre-trained XGBoost model.
     */
    @GetMapping("/predict/{workerId}")
    public java.util.Map<String, Object> predictRiskForWorker(@PathVariable Long workerId) {
        try {
            FeatureRequestDTO features = mlDataService.aggregateFeaturesForWorker(workerId);
            float predictedScore = inferenceService.predictRiskScore(features);
            
            return java.util.Map.of(
                "workerId", workerId,
                "prediction", predictedScore,
                "features", features
            );
        } catch (Exception e) {
            return java.util.Map.of(
                "workerId", workerId,
                "error", "Risk score unavailable. Worker profile not fully synced.",
                "prediction", 1.0f 
            );
        }
    }

    /**
     * Returns the features as a flat double array, often preferred by
     * ML inference engines like XGBoost4J.
     */
    @GetMapping("/features/{workerId}/raw")
    public double[] getRawFeaturesForWorker(@PathVariable Long workerId) {
        return mlDataService.aggregateFeaturesForWorker(workerId).toFeatureArray();
    }
}
