package com.fluxusbackend.fluxusbackend.mermamanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.shared.domain.model.valueobjects.CompanyId;

public record ListMermasByCompanyQuery(CompanyId companyId) {
    public ListMermasByCompanyQuery {
        if (companyId == null) throw new IllegalArgumentException("companyId is required");
    }
}
