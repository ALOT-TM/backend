package com.fluxusbackend.shrinkage.application.internal.queryservices;

import com.fluxusbackend.shrinkage.domain.model.aggregates.Shrinkage;
import com.fluxusbackend.shrinkage.domain.model.queries.GetShrinkageByIdQuery;
import com.fluxusbackend.shrinkage.domain.model.queries.ListShrinkagesByStatusQuery;
import com.fluxusbackend.shrinkage.domain.model.queries.ListShrinkagesByCompanyQuery;
import com.fluxusbackend.shrinkage.domain.services.ShrinkageQueryService;
import com.fluxusbackend.shrinkage.infrastructure.persistence.jpa.repositories.ShrinkageRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShrinkageQueryServiceImpl implements ShrinkageQueryService {

    private final ShrinkageRepository repository;

    public ShrinkageQueryServiceImpl(ShrinkageRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Shrinkage> handle(GetShrinkageByIdQuery query) {
        return repository.findById(query.shrinkageId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shrinkage> handle(ListShrinkagesByStatusQuery query) {
        return repository.findByStatus(query.status());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shrinkage> handle(ListShrinkagesByCompanyQuery query) {
        var companyId = query.companyId();
        return repository.findByCompanyIdValue(companyId.value());
    }
}


