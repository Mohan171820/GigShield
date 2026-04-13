package com.gigshield.service;

import com.gigshield.dto.ClaimPredictionResponseDTO;
import com.gigshield.dto.FeatureRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class XGBoostInferenceService {

    private static final Logger log = LoggerFactory.getLogger(XGBoostInferenceService.class);
    private final RestTemplate restTemplate;

    @Value("${gigshield.api.ml.base-url}")
    private String mlBaseUrl;

    @Value("${gigshield.api.ml.mock-mode:false}")
    private boolean mockMode;

    /**
     * Sends features to the remote FastAPI model and returns the risk score.
     * 
     * @param features The 15 aggregated features.
     * @return The predictive score (e.g., risk probability).
     */
    public float predictRiskScore(FeatureRequestDTO features) {
        if (mockMode) {
            log.info("[ML-MOCK] Returning fixed low-risk score (0.25).");
            return 0.25f;
        }

        try {
            if (mlBaseUrl == null) throw new RuntimeException("ML Base URL is not configured");
            log.info("[ML-REMOTE] Calling FastAPI at: {}", mlBaseUrl);
            
            // Sending the 15 features as a JSON body
            Float riskScore = restTemplate.postForObject(mlBaseUrl, features, Float.class);
            
            if (riskScore != null) {
                log.info("[ML-REMOTE] Success! Risk Score received: {}", riskScore);
                return riskScore;
            } else {
                log.warn("[ML-REMOTE] Received null response. Falling back to safe score.");
                return 1.0f;
            }

        } catch (Exception e) {
            log.error("[ML-REMOTE] FAILED to call FastAPI: {}. Falling back to 1.0 risk.", e.getMessage());
            // Fallback to a "Standard" risk score so the demo continues if the other laptop is off
            return 1.0f; 
        }
    }

    /**
     * Checks if a user's disruption report (Claim) is eligible for a payout
     * based on current weather features.
     * 
     * @param features The 7 weather features.
     * @return ClaimPredictionResponseDTO containing eligibility and payout amount.
     */
    public ClaimPredictionResponseDTO predictClaimEligibility(FeatureRequestDTO features) {
        
        // --- 1. Fraud Detection Layer ---
        // A. Device spoofing detection
        if (features.isGps_spoofed()) {
            log.warn("[FRAUD-DETECT] Device level anomaly detected. Flagging for review.");
            return ClaimPredictionResponseDTO.builder()
                    .eligible(false)
                    .fraudFlagged(true)
                    .fraudReason("Mock GPS/Device Location Spoofing Detected")
                    .status("error")
                    .build();
        }
        
        // B. Spatial Anomaly Detection (Worker reports 30mm rain, but Zone is 0mm)
        boolean isRainAnomaly = features.getRain_mm() > 20.0 && features.getZone_avg_rain_mm() < 2.0;
        if (isRainAnomaly) {
            log.warn("[FRAUD-DETECT] Spatial weather anomaly detected. Flagging for review.");
            return ClaimPredictionResponseDTO.builder()
                    .eligible(false)
                    .fraudFlagged(true)
                    .fraudReason("Individual reported metrics deviate significantly from Zonal truth data.")
                    .status("error")
                    .build();
        }

        // --- 2. Inference Layer ---
        if (mockMode) {
            log.info("[ML-MOCK] Returning fixed eligible claim (₹350).");
            return ClaimPredictionResponseDTO.builder()
                    .eligible(true)
                    .claim_amount(350.0)
                    .confidence(0.9)
                    .status("success")
                    .message("Mock Check: ELIGIBLE")
                    .build();
        }

        try {
            log.info("[ML-REMOTE] Checking Claim Eligibility at: {}", mlBaseUrl);
            
            // The exact same endpoint handles both risk scores and claim payouts
            ClaimPredictionResponseDTO response = restTemplate.postForObject(mlBaseUrl, features, ClaimPredictionResponseDTO.class);
            
            if (response != null) {
                log.info("[ML-REMOTE] Claim Check SUCCESS! Eligible: {} | Payout: ₹{}", 
                        response.isEligible(), response.getClaim_amount());
                return response;
            } else {
                throw new RuntimeException("Received null response from ML model.");
            }

        } catch (Exception e) {
            log.error("[ML-REMOTE] FAILED to check Claim eligibility: {}. Falling back to manual review.", e.getMessage());
            return ClaimPredictionResponseDTO.builder()
                    .eligible(false)
                    .status("error")
                    .message("ML system offline. Manual review required.")
                    .build();
        }
    }
}
