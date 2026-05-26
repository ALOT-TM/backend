package com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.beneficiary.domain.model.aggregates.InstitutionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionTypeRepository extends JpaRepository<InstitutionType, Long> {
}
