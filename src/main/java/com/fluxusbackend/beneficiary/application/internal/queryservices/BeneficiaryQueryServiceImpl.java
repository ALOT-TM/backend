package com.fluxusbackend.beneficiary.application.internal.queryservices;

import com.fluxusbackend.beneficiary.domain.model.aggregates.BeneficiaryInstitution;
import com.fluxusbackend.beneficiary.domain.model.queries.GetBeneficiaryByIdQuery;
import com.fluxusbackend.beneficiary.domain.model.queries.ListBeneficiaryInstitutionsQuery;
import com.fluxusbackend.beneficiary.domain.services.BeneficiaryQueryService;
import com.fluxusbackend.beneficiary.infrastructure.persistence.jpa.repositories.BeneficiaryInstitutionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BeneficiaryQueryServiceImpl implements BeneficiaryQueryService {

    private final BeneficiaryInstitutionRepository repository;

    public BeneficiaryQueryServiceImpl(BeneficiaryInstitutionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BeneficiaryInstitution> handle(GetBeneficiaryByIdQuery query) {
        return repository.findById(query.beneficiaryId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiaryInstitution> handle(ListBeneficiaryInstitutionsQuery query) {
        return repository.findAll();
    }
}


