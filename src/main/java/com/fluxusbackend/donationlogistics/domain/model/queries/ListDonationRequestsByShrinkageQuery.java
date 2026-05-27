package com.fluxusbackend.donationlogistics.domain.model.queries;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ShrinkageReferenceId;

public record ListDonationRequestsByShrinkageQuery(ShrinkageReferenceId shrinkageReferenceId) {
    public ListDonationRequestsByShrinkageQuery {
        if (shrinkageReferenceId == null) throw new IllegalArgumentException("shrinkageReferenceId is required");
    }
}
