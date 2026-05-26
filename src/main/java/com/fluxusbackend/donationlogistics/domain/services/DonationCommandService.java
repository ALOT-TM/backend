package com.fluxusbackend.donationlogistics.domain.services;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.Donation;
import com.fluxusbackend.donationlogistics.domain.model.commands.ConfirmDonationReceptionCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CreateDonationCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.MarkDonationDeliveredCommand;

public interface DonationCommandService {
    Donation handle(CreateDonationCommand command);

    Donation handle(MarkDonationDeliveredCommand command);

    Donation handle(ConfirmDonationReceptionCommand command);
}


