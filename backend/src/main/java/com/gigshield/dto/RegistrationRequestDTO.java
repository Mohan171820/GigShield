package com.gigshield.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

/**
 * Robust DTO for the 3-step registration flow.
 * Supports both snake_case (frontend) and camelCase (backend) mappings.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequestDTO {
    
    @com.fasterxml.jackson.annotation.JsonProperty("workerId")
    private Long workerId;

    private String name;
    
    @JsonAlias({"phone_number", "phoneNumber"})
    private String phoneNumber;
    
    private String city;
    
    @JsonAlias({"upi_id", "upiId"})
    private String upiId;
    
    private String password;
    
    private String platform;
    
    private String zone; 
    
    @JsonAlias({"weekly_active_hours", "weeklyActiveHours"})
    private Double weeklyActiveHours;
    
    @JsonAlias({"tenure_weeks", "tenureWeeks"})
    private Integer tenureWeeks;

    @JsonAlias({"orders_this_month", "ordersThisMonth"})
    private Integer ordersThisMonth;

    @JsonAlias({"avg_daily_earnings", "avgDailyEarnings"})
    private Double avgDailyEarnings;
    
    @JsonAlias({"coverage_types", "coverageTypes"})
    private List<String> coverageTypes; 
    
    private String tier = "STANDARD"; 
}
