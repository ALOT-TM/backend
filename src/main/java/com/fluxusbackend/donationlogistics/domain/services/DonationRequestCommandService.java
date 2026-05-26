package com.fluxusbackend.donationlogistics.domain.services;

import com.fluxusbackend.donationlogistics.domain.model.aggregates.DonationRequest;
import com.fluxusbackend.donationlogistics.domain.model.commands.AcceptDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CancelDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.CreateDonationRequestCommand;
import com.fluxusbackend.donationlogistics.domain.model.commands.RejectDonationRequestCommand;

public interface DonationRequestCommandService {
    DonationRequest handle(CreateDonationRequestCommand command);

    DonationRequest handle(AcceptDonationRequestCommand command);

    DonationRequest handle(RejectDonationRequestCommand command);

    DonationRequest handle(CancelDonationRequestCommand command);
}

