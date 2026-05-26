package com.fluxusbackend.donationlogistics.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record DonationId(@Column(name = "donation_id", nullable = false) Long value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public DonationId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Donation id must be positive");
        }
    }

    @JsonValue
    public Long value() {
        return value;
    }
}


