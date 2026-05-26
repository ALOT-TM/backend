package com.fluxusbackend.donationlogistics.domain.model.queries;

import com.fluxusbackend.donationlogistics.domain.model.enums.DonationStatus;
import java.util.Objects;

public record ListDonationsByStatusQuery(DonationStatus status) {
    public ListDonationsByStatusQuery {
        Objects.requireNonNull(status, "Status is required");
    }
}


