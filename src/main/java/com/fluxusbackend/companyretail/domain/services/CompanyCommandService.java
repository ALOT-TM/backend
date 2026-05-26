package com.fluxusbackend.companyretail.domain.services;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
import com.fluxusbackend.companyretail.domain.model.commands.CreateCompanyCommand;

public interface CompanyCommandService {
    RetailCompany handle(CreateCompanyCommand command);
}

