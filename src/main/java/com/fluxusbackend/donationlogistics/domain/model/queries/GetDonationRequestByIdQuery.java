package com.fluxusbackend.donationlogistics.domain.model.queries;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationRequestId;

public record GetDonationRequestByIdQuery(DonationRequestId requestId) {
    public GetDonationRequestByIdQuery {
        if (requestId == null) throw new IllegalArgumentException("requestId is required");
    }
}

