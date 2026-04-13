package com.gigshield.repository;

import com.gigshield.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    long countByWorkerId(Long workerId);
    List<Claim> findByWorkerIdAndStatus(Long workerId, Claim.ClaimStatus status);
    List<Claim> findByWorkerIdOrderByCreatedAtDesc(Long workerId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(c.payoutAmount) FROM Claim c WHERE c.status = 'PAID'")
    java.math.BigDecimal sumTotalPayouts();

    java.util.List<Claim> findTop10ByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(c.payoutAmount), 0) FROM Claim c WHERE c.policy.id = :policyId AND c.status = 'PAID'")
    java.math.BigDecimal sumPaidPayoutsByPolicyId(@org.springframework.data.repository.query.Param("policyId") Long policyId);
}
