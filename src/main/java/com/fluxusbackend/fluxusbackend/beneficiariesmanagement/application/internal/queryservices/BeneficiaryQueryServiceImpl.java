package com.fluxusbackend.fluxusbackend.beneficiariesmanagement.application.internal.queryservices;

import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.aggregates.Beneficiary;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.queries.GetBeneficiaryByIdQuery;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.model.queries.ListBeneficiariesByStatusQuery;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.domain.services.BeneficiaryQueryService;
import com.fluxusbackend.fluxusbackend.beneficiariesmanagement.infrastructure.persistence.jpa.repositories.BeneficiaryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BeneficiaryQueryServiceImpl implements BeneficiaryQueryService {

    private final BeneficiaryRepository repository;

    public BeneficiaryQueryServiceImpl(BeneficiaryRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Beneficiary> handle(GetBeneficiaryByIdQuery query) {
        return repository.findById(query.beneficiaryId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Beneficiary> handle(ListBeneficiariesByStatusQuery query) {
        return repository.findByStatus(query.status());
    }
}

