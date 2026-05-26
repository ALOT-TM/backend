package com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaryInstitutionRepository extends JpaRepository<BeneficiaryInstitution, Long> {
}


