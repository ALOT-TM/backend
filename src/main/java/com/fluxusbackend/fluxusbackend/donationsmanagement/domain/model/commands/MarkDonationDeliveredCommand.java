package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.DeliveryDate;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.DonationId;
import java.util.Objects;

public record MarkDonationDeliveredCommand(DonationId donationId, DeliveryDate deliveryDate) {
    public MarkDonationDeliveredCommand {
        Objects.requireNonNull(donationId, "Donation id is required");
        Objects.requireNonNull(deliveryDate, "Delivery date is required");
    }
}

