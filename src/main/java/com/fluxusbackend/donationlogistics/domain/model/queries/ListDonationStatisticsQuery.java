package com.fluxusbackend.donationlogistics.domain.model.queries;

import com.fluxusbackend.shared.domain.model.valueobjects.CompanyId;

public record ListDonationStatisticsQuery(
    CompanyId companyId
) {
}

