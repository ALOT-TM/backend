package com.fluxusbackend.donationlogistics.domain.model.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record DonationRequestId(@Column(name = "request_id") Long value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public DonationRequestId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Donation request id must be positive");
        }
    }

    @JsonValue
    public Long value() {
        return value;
    }
}

