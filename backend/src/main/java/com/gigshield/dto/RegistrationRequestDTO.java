package com.gigshield.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

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

    public Long getWorkerId() { return workerId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCity() { return city; }
    public String getUpiId() { return upiId; }
    public String getPassword() { return password; }
    public String getPlatform() { return platform; }
    public String getZone() { return zone; }
    public Double getWeeklyActiveHours() { return weeklyActiveHours; }
    public Integer getTenureWeeks() { return tenureWeeks; }
    public Integer getOrdersThisMonth() { return ordersThisMonth; }
    public Double getAvgDailyEarnings() { return avgDailyEarnings; }
    public String getTier() { return tier; }
}
