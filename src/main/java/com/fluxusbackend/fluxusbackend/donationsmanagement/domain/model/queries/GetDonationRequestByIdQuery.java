package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.DonationRequestId;

public record GetDonationRequestByIdQuery(DonationRequestId requestId) {
    public GetDonationRequestByIdQuery {
        if (requestId == null) throw new IllegalArgumentException("requestId is required");
    }
}
