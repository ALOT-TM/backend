package com.fluxusbackend.fluxusbackend.mermamanagement.application.internal.queryservices;

import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.aggregates.Merma;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.GetMermaByIdQuery;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.ListMermasByStatusQuery;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries.ListMermasByCompanyQuery;
import com.fluxusbackend.fluxusbackend.mermamanagement.domain.services.MermaQueryService;
import com.fluxusbackend.fluxusbackend.mermamanagement.infrastructure.persistence.jpa.repositories.MermaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MermaQueryServiceImpl implements MermaQueryService {

    private final MermaRepository repository;

    public MermaQueryServiceImpl(MermaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Merma> handle(GetMermaByIdQuery query) {
        return repository.findById(query.mermaId().value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Merma> handle(ListMermasByStatusQuery query) {
        return repository.findByStatus(query.status());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Merma> handle(ListMermasByCompanyQuery query) {
        var companyId = query.companyId();
        return repository.findByCompanyIdValue(companyId.value());
    }
}

