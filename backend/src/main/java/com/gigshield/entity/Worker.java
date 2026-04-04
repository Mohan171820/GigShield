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

    public enum Platform {
        BLINKIT, ZEPTO, SWIGGY_INSTAMART
    }
}
