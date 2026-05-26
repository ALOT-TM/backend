package com.fluxusbackend.donationlogistics.domain.model.queries;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.MermaReferenceId;

public record ListDonationRequestsByMermaQuery(MermaReferenceId mermaReferenceId) {
    public ListDonationRequestsByMermaQuery {
        if (mermaReferenceId == null) throw new IllegalArgumentException("mermaReferenceId is required");
    }
}

