package com.fluxusbackend.donationlogistics.domain.model.queries;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;

public record ListDonationRequestsByBeneficiaryQuery(BeneficiaryReferenceId beneficiaryReferenceId) {
    public ListDonationRequestsByBeneficiaryQuery {
        if (beneficiaryReferenceId == null) throw new IllegalArgumentException("beneficiaryReferenceId is required");
    }
}

