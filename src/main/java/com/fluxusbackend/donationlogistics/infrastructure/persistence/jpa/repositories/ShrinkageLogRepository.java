package com.fluxusbackend.donationlogistics.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.ShrinkageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShrinkageLogRepository extends JpaRepository<ShrinkageLog, Long> {
}
