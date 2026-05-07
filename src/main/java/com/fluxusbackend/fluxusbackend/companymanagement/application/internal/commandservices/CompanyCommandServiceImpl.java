package com.fluxusbackend.fluxusbackend.companymanagement.application.internal.commandservices;

import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.aggregates.Company;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.commands.CreateCompanyCommand;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.services.CompanyCommandService;
import com.fluxusbackend.fluxusbackend.companymanagement.infrastructure.persistence.jpa.repositories.CompanyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CompanyCommandServiceImpl implements CompanyCommandService {

    private final CompanyRepository repository;

    public CompanyCommandServiceImpl(CompanyRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Company handle(CreateCompanyCommand command) {
        var company = new Company(command.name(), command.headquarters());
        return repository.save(company);
    }
}
