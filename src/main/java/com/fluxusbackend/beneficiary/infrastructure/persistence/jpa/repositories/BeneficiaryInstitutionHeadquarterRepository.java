package com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitutionHeadquarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaryInstitutionHeadquarterRepository extends JpaRepository<BeneficiaryInstitutionHeadquarter, Long> {
}
