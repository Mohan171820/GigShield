package com.gigshield.repository;

import com.gigshield.entity.ZoneMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ZoneMetricsRepository extends JpaRepository<ZoneMetrics, Long> {
    
    @Query("SELECT z FROM ZoneMetrics z WHERE z.city = :city AND z.zone = :zone AND z.date >= :startDate")
    List<ZoneMetrics> findRecentMetrics(
        @Param("city") String city, 
        @Param("zone") String zone, 
        @Param("startDate") LocalDate startDate
    );

    @Query("SELECT COUNT(z) FROM ZoneMetrics z WHERE z.city = :city AND z.zone = :zone AND z.date >= :startDate AND z.disruptionEventOccurred = true")
    long countDisruptions(
        @Param("city") String city, 
        @Param("zone") String zone, 
        @Param("startDate") LocalDate startDate
    );
}
