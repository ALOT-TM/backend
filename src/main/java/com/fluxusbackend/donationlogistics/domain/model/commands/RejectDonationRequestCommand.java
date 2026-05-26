package com.fluxusbackend.donationlogistics.domain.model.commands;

import com.fluxusbackend.donationlogistics.domain.model.valueobjects.DonationRequestId;
import jakarta.validation.constraints.NotNull;

public record RejectDonationRequestCommand(@NotNull DonationRequestId requestId) {
}

