package com.gigshield.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimPredictionResponseDTO {
    
    private boolean eligible;
    
    private double claim_amount;
    
    private double confidence;
    
    private String status;
    
    private String message;
    
    private String timestamp;

    private boolean fraudFlagged;
    
    private String fraudReason;
}
