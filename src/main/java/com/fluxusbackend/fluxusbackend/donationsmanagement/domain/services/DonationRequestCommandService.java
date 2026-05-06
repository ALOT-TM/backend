package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.services;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.AcceptDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CancelDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.CreateDonationRequestCommand;
import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands.RejectDonationRequestCommand;

public interface DonationRequestCommandService {
    DonationRequest handle(CreateDonationRequestCommand command);

    DonationRequest handle(AcceptDonationRequestCommand command);

    DonationRequest handle(RejectDonationRequestCommand command);

    DonationRequest handle(CancelDonationRequestCommand command);
}
