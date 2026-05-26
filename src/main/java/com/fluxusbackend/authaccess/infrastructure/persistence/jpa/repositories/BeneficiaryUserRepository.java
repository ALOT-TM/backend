package com.fluxusbackend.authaccess.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.authaccess.domain.model.aggregates.BeneficiaryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeneficiaryUserRepository extends JpaRepository<BeneficiaryUser, Long> {
}
