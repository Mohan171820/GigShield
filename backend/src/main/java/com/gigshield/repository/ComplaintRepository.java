package com.gigshield.repository;

import com.gigshield.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    List<Complaint> findByWorkerIdOrderByCreatedAtDesc(Long workerId);

    long countByDeviceFingerprintOrIpAddressAndCreatedAtAfter(String deviceFingerprint, String ipAddress, LocalDateTime date);
}
