package com.fluxusbackend.fluxusbackend.donationsmanagement.application.internal.queryservices;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.Donation;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.GetDonationByIdQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationsByBeneficiaryQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationsByStatusQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationQueryService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.infrastructure.persistence.jpa.repositories.DonationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationQueryServiceImpl implements DonationQueryService {

    private final DonationRepository repository;

    public DonationQueryServiceImpl(DonationRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Donation> handle(GetDonationByIdQuery query) {
        return repository.findById(query.donationId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Donation> handle(ListDonationsByStatusQuery query) {
        return repository.findByStatus(query.status());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Donation> handle(ListDonationsByBeneficiaryQuery query) {
        return repository.findByBeneficiaryReferenceIdValue(query.beneficiaryReferenceId().value());
    }
}

