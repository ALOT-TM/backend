package com.fluxusbackend.companyretail.application.internal.commandservices;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
import com.fluxusbackend.companyretail.domain.model.commands.CreateCompanyCommand;
import com.fluxusbackend.companyretail.domain.services.CompanyCommandService;
import com.fluxusbackend.companyretail.infrastructure.persistence.jpa.repositories.RetailCompanyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CompanyCommandServiceImpl implements CompanyCommandService {

    private final RetailCompanyRepository repository;

    public CompanyCommandServiceImpl(RetailCompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public RetailCompany handle(CreateCompanyCommand command) {
        var company = new RetailCompany(command.name());
        return repository.save(company);
    }
}

