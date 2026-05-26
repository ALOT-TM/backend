package com.fluxusbackend.companyretail.application.internal.queryservices;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
import com.fluxusbackend.companyretail.domain.model.queries.GetCompanyByIdQuery;
import com.fluxusbackend.companyretail.domain.model.queries.ListCompaniesQuery;
import com.fluxusbackend.companyretail.domain.services.CompanyQueryService;
import com.fluxusbackend.companyretail.infrastructure.persistence.jpa.repositories.RetailCompanyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CompanyQueryServiceImpl implements CompanyQueryService {

    private final RetailCompanyRepository repository;

    public CompanyQueryServiceImpl(RetailCompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<RetailCompany> handle(GetCompanyByIdQuery query) {
        return repository.findById(query.companyId().value());
    }

    @Override
    public List<RetailCompany> handle(ListCompaniesQuery query) {
        return repository.findAll();
    }
}

