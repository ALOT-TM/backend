package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.BeneficiaryReferenceId;

public record ListDonationRequestsByBeneficiaryQuery(BeneficiaryReferenceId beneficiaryReferenceId) {
    public ListDonationRequestsByBeneficiaryQuery {
        if (beneficiaryReferenceId == null) throw new IllegalArgumentException("beneficiaryReferenceId is required");
    }
}
