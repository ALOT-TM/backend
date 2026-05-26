package com.fluxusbackend.companyretail.domain.model.queries;

import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import java.util.Objects;

public record GetCompanyByIdQuery(CompanyId companyId) {
    public GetCompanyByIdQuery {
        Objects.requireNonNull(companyId, "Company id is required");
    }
}

