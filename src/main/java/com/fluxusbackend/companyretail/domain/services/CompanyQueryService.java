package com.fluxusbackend.companyretail.domain.services;

import com.fluxusbackend.companyretail.domain.model.aggregates.RetailCompany;
import com.fluxusbackend.companyretail.domain.model.queries.GetCompanyByIdQuery;
import com.fluxusbackend.companyretail.domain.model.queries.ListCompaniesQuery;
import java.util.List;
import java.util.Optional;

public interface CompanyQueryService {
    Optional<RetailCompany> handle(GetCompanyByIdQuery query);

    List<RetailCompany> handle(ListCompaniesQuery query);
}

