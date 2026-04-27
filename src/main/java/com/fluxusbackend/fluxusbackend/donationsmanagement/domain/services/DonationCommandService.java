package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.Donation;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.ConfirmDonationReceptionCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CreateDonationCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.MarkDonationDeliveredCommand;

public interface DonationCommandService {
    Donation handle(CreateDonationCommand command);

    Donation handle(MarkDonationDeliveredCommand command);

    Donation handle(ConfirmDonationReceptionCommand command);
}

