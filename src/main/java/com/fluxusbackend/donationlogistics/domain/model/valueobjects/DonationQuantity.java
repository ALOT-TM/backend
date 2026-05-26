package com.fluxusbackend.donationlogistics.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record DonationQuantity(@Column(name = "donation_quantity", nullable = false) int amount) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public DonationQuantity {
        if (amount <= 0) {
            throw new IllegalArgumentException("Donation quantity must be greater than zero");
        }
    }

    @JsonValue
    public int amount() {
        return amount;
    }
}


