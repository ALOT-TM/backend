package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.MermaReferenceId;

public record ListDonationRequestsByMermaQuery(MermaReferenceId mermaReferenceId) {
    public ListDonationRequestsByMermaQuery {
        if (mermaReferenceId == null) throw new IllegalArgumentException("mermaReferenceId is required");
    }
}
