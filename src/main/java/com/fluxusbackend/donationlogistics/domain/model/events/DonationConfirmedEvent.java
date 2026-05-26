package com.fluxusbackend.donationlogistics.domain.model.events;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationId;
import java.time.Instant;
import java.util.Objects;

public final class DonationConfirmedEvent {

    private final DonationId donationId;
    private final Instant occurredOn;

    public DonationConfirmedEvent(DonationId donationId, Instant occurredOn) {
        this.donationId = Objects.requireNonNull(donationId, "Donation id is required");
        this.occurredOn = Objects.requireNonNull(occurredOn, "Occurred time is required");
    }

    public DonationId getDonationId() {
        return donationId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}


