package com.fluxusbackend.fluxusbackend.companymanagement.domain.services;

import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.aggregates.Company;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.commands.CreateCompanyCommand;

public interface CompanyCommandService {
    Company handle(CreateCompanyCommand command);
}
