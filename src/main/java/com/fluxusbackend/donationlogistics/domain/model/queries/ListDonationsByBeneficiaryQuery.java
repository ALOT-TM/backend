package com.fluxusbackend.donationlogistics.domain.model.queries;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.BeneficiaryReferenceId;
import java.util.Objects;

public record ListDonationsByBeneficiaryQuery(BeneficiaryReferenceId beneficiaryReferenceId) {
    public ListDonationsByBeneficiaryQuery {
        Objects.requireNonNull(beneficiaryReferenceId, "Beneficiary reference id is required");
    }
}


