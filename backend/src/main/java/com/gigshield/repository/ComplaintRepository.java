package com.gigshield.repository;

import com.gigshield.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    /**
     * Fetches all complaints for a specific worker, ordered by recency.
     */
    List<Complaint> findByWorkerIdOrderByCreatedAtDesc(Long workerId);
}
