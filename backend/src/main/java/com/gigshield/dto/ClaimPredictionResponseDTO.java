package com.gigshield.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class ClaimPredictionResponseDTO {
    
    private boolean eligible;
    private double claim_amount;
    private double confidence;
    private String status;
    private String message;
    private boolean fraudFlagged;
    private String fraudReason;

    public ClaimPredictionResponseDTO() {}

    public ClaimPredictionResponseDTO(boolean eligible, double claim_amount, double confidence, 
                                      String status, String message, boolean fraudFlagged, String fraudReason) {
        this.eligible = eligible;
        this.claim_amount = claim_amount;
        this.confidence = confidence;
        this.status = status;
        this.message = message;
        this.fraudFlagged = fraudFlagged;
        this.fraudReason = fraudReason;
    }

    public boolean isEligible() { return eligible; }
    public double getConfidence() { return confidence; }
    public double getClaim_amount() { return claim_amount; }
    public boolean isFraudFlagged() { return fraudFlagged; }
    public String getStatus() { return status; }
    public String getFraudReason() { return fraudReason; }
    public String getMessage() { return message; }

    public static ClaimPredictionResponseDTOBuilder builder() {
        return new ClaimPredictionResponseDTOBuilder();
    }

    public static class ClaimPredictionResponseDTOBuilder {
        private boolean eligible;
        private double claimAmount;
        private double confidence;
        private String status;
        private String message;
        private boolean fraudFlagged;
        private String fraudReason;

        public ClaimPredictionResponseDTOBuilder eligible(boolean e) { this.eligible = e; return this; }
        public ClaimPredictionResponseDTOBuilder claim_amount(double a) { this.claimAmount = a; return this; }
        public ClaimPredictionResponseDTOBuilder confidence(double c) { this.confidence = c; return this; }
        public ClaimPredictionResponseDTOBuilder status(String s) { this.status = s; return this; }
        public ClaimPredictionResponseDTOBuilder message(String m) { this.message = m; return this; }
        public ClaimPredictionResponseDTOBuilder fraudFlagged(boolean f) { this.fraudFlagged = f; return this; }
        public ClaimPredictionResponseDTOBuilder fraudReason(String r) { this.fraudReason = r; return this; }
        public ClaimPredictionResponseDTO build() {
            return new ClaimPredictionResponseDTO(eligible, claimAmount, confidence, status, message, fraudFlagged, fraudReason);
        }
    }
}
