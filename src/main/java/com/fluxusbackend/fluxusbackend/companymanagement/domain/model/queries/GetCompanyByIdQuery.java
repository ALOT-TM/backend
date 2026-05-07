package com.fluxusbackend.fluxusbackend.companymanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import java.util.Objects;

public record GetCompanyByIdQuery(CompanyId companyId) {
    public GetCompanyByIdQuery {
        Objects.requireNonNull(companyId, "Company id is required");
    }
}
