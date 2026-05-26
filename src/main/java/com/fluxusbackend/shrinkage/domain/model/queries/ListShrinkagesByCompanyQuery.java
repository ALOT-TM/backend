package com.fluxusbackend.shrinkage.domain.model.queries;

import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;

public record ListShrinkagesByCompanyQuery(CompanyId companyId) {
    public ListShrinkagesByCompanyQuery {
        if (companyId == null) throw new IllegalArgumentException("companyId is required");
    }
}

