package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.queries;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.enums.DonationStatus;
import java.util.Objects;

public record ListDonationsByStatusQuery(DonationStatus status) {
    public ListDonationsByStatusQuery {
        Objects.requireNonNull(status, "Status is required");
    }
}

