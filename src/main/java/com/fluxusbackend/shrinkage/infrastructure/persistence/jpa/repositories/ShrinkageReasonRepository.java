package com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.shrinkage.domain.model.aggregates.ShrinkageReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShrinkageReasonRepository extends JpaRepository<ShrinkageReason, Long> {
	boolean existsByNameIgnoreCase(String name);
}
