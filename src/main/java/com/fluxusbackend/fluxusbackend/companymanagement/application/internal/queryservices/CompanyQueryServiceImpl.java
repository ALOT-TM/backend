package com.fluxusbackend.fluxusbackend.companymanagement.application.internal.queryservices;

import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.aggregates.Company;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.queries.GetCompanyByIdQuery;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.queries.ListCompaniesQuery;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.services.CompanyQueryService;
import com.fluxusbackend.fluxusbackend.companymanagement.infrastructure.persistence.jpa.repositories.CompanyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CompanyQueryServiceImpl implements CompanyQueryService {

    private final CompanyRepository repository;

    public CompanyQueryServiceImpl(CompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Company> handle(GetCompanyByIdQuery query) {
        return repository.findById(query.companyId().value());
    }

    @Override
    public List<Company> handle(ListCompaniesQuery query) {
        return repository.findAll();
    }
}
