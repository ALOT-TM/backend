package com.fluxusbackend.donationlogistics.domain.model.queries;

import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;
import java.util.Objects;

public record ListDonationsByCompanyQuery(CompanyId companyId) {
    public ListDonationsByCompanyQuery {
        Objects.requireNonNull(companyId, "Company id is required");
    }
}
