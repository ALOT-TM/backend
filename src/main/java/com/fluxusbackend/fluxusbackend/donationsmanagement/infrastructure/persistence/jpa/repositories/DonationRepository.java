package com.fluxusbackend.fluxusbackend.donationsmanagement.infrastructure.persistence.jpa.repositories;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.Donation;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.enums.DonationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByStatus(DonationStatus status);

    List<Donation> findByBeneficiaryReferenceIdValue(Long value);
}

