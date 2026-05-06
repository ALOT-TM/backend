package com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.commands;

import com.fluxusbackend.fluxusbackend.donationsmanagement.domain.model.valueobjects.DonationRequestId;
import jakarta.validation.constraints.NotNull;

public record AcceptDonationRequestCommand(@NotNull DonationRequestId requestId) {
}
