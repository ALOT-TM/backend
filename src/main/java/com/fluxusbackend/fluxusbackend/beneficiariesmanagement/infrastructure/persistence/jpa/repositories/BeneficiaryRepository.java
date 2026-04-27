package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.aggregates.Beneficiary;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.enums.BeneficiaryStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByStatus(BeneficiaryStatus status);
}

