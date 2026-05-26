package com.fluxusbackend.donationlogistics.domain.model.commands;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationId;
import com.fluxusbackend.donationlogistics.domain.model.valueobjects.ReceptionDate;
import java.util.Optional;

public record ConfirmDonationReceptionCommand(DonationId donationId, ReceptionDate receptionDate, Optional<String> comment) {
    public ConfirmDonationReceptionCommand {
        if (donationId == null) {
            throw new IllegalArgumentException("Donation id is required");
        }
        if (receptionDate == null) {
            throw new IllegalArgumentException("Reception date is required");
        }
        if (comment == null) {
            comment = Optional.empty();
        }
    }
}


