package com.gigshield.repository;

import com.gigshield.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findByWorkerIdAndStatus(Long workerId, Policy.PolicyStatus status);
    long countByStatus(Policy.PolicyStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.finalPremium) FROM Policy p")
    java.math.BigDecimal sumTotalPremiums();
}
