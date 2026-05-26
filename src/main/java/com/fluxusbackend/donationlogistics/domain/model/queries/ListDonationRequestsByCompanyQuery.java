package com.fluxusbackend.donationlogistics.domain.model.queries;

import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;

public record ListDonationRequestsByCompanyQuery(
    CompanyId companyId
) {
}

