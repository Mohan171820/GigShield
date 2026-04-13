package com.gigshield.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@Entity
@Table(name = "workers")
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String upiId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city; 

    @Column(nullable = false)
    private String zone; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform; 

   
    @Column(nullable = false)
    private Double weeklyActiveHours;

    private Integer tenureWeeks;

    @Column(nullable = true)
    private Integer ordersThisMonth;

    @Column(nullable = true)
    private Double avgDailyEarnings;

    @Column(nullable = true) // Temporarily true to allow column creation
    private String password;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Policy> policies;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Claim> claims;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (tenureWeeks == null) tenureWeeks = 0;
    }

    public Long getId() { return id; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUpiId() { return upiId; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getZone() { return zone; }
    public Platform getPlatform() { return platform; }
    public String getPassword() { return password; }
    public Integer getTenureWeeks() { return tenureWeeks; }

    public void setPhoneNumber(String n) { this.phoneNumber = n; }
    public void setName(String n) { this.name = n; }
    public void setCity(String city) { this.city = city; }
    public void setZone(String zone) { this.zone = zone; }
    public void setUpiId(String u) { this.upiId = u; }
    public void setPassword(String p) { this.password = p; }
    public void setWeeklyActiveHours(Double h) { this.weeklyActiveHours = h; }
    public void setTenureWeeks(Integer w) { this.tenureWeeks = w; }
    public void setOrdersThisMonth(Integer o) { this.ordersThisMonth = o; }
    public void setAvgDailyEarnings(Double e) { this.avgDailyEarnings = e; }
    public void setPlatform(Platform p) { this.platform = p; }

    public Double getWeeklyActiveHours() { return weeklyActiveHours; }
    public Integer getOrdersThisMonth() { return ordersThisMonth; }
    public Double getAvgDailyEarnings() { return avgDailyEarnings; }

    public enum Platform {
        BLINKIT, ZEPTO, SWIGGY_INSTAMART
    }
}
