package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.DonationId;
import java.util.Objects;

public record GetDonationByIdQuery(DonationId donationId) {
    public GetDonationByIdQuery {
        Objects.requireNonNull(donationId, "Donation id is required");
    }
}

