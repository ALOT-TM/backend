package com.fluxusbackend.fluxusbackend.companymanagement.domain.services;

import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.aggregates.Company;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.queries.GetCompanyByIdQuery;
import com.fluxusbackend.fluxusbackend.companymanagement.domain.model.queries.ListCompaniesQuery;
import java.util.List;
import java.util.Optional;

public interface CompanyQueryService {
    Optional<Company> handle(GetCompanyByIdQuery query);

    List<Company> handle(ListCompaniesQuery query);
}
