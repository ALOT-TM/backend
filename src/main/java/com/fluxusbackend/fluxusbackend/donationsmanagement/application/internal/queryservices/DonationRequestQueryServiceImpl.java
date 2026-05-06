package com.fluxusbackend.fluxusbackend.donationsmanagement.application.internal.queryservices;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.GetDonationRequestByIdQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationRequestsByBeneficiaryQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationRequestsByMermaQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationRequestsByCompanyQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries.ListDonationRequestsByProductNameQuery;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services.DonationRequestQueryService;
import com.fluxusbackend.fluxusbackend.donationsmanagement.infrastructure.persistence.jpa.repositories.DonationRequestRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationRequestQueryServiceImpl implements DonationRequestQueryService {

    private final DonationRequestRepository repository;

    public DonationRequestQueryServiceImpl(DonationRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DonationRequest> handle(GetDonationRequestByIdQuery query) {
        return repository.findById(query.requestId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationRequest> handle(ListDonationRequestsByBeneficiaryQuery query) {
        return repository.findByBeneficiaryId(query.beneficiaryReferenceId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationRequest> handle(ListDonationRequestsByMermaQuery query) {
        return repository.findByMermaId(query.mermaReferenceId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationRequest> handle(ListDonationRequestsByCompanyQuery query) {
        return repository.findByCompanyId(query.companyId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DonationRequest> handle(ListDonationRequestsByProductNameQuery query) {
        return repository.findByProductName(query.productName());
    }
}
