package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.shared.domain.model.valueobjects.CompanyId;

public record ListDonationRequestsByCompanyQuery(
    CompanyId companyId
) {
}
